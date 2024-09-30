package building;
import java.io.BufferedWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.LayoutFocusTraversalPolicy;

import myfileio.MyFileIO;
import genericqueue.GenericQueue;

// TODO: Auto-generated Javadoc

/*
 * Class belongs to: Nikash
 * Reviewed by: Henry
 * Additional Comments:
 */

/**
 * The Class Building.
 */
// TODO: Auto-generated Javadoc
public class Building {
	
	/**  Constants for direction. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	
	/**  The fh - used by LOGGER to write the log messages to a file. */
	private FileHandler fh;
	
	/**  The fio for writing necessary files for data analysis. */
	private MyFileIO fio;
	
	/**  File that will receive the information for data analysis. */
	private File passDataFile;

	/**  passSuccess holds all Passengers who arrived at their destination floor. */
	private ArrayList<Passengers> passSuccess;
	
	/**  gaveUp holds all Passengers who gave up and did not use the elevator. */
	private ArrayList<Passengers> gaveUp;
	
	/**  The number of floors - must be initialized in constructor. */
	private final int NUM_FLOORS;
	
	/**  The size of the up/down queues on each floor. */
	private final int FLOOR_QSIZE = 10;	
	
	/** passQ holds the time-ordered queue of Passengers, initialized at the start 
	 *  of the simulation. At the end of the simulation, the queue will be empty.
	 */
	private GenericQueue<Passengers> passQ;

	/**  The size of the queue to store Passengers at the start of the simulation. */
	private final int PASSENGERS_QSIZE = 1000;	

	/**  The number of elevators - must be initialized in constructor. */
	private final int NUM_ELEVATORS;
	
	/** The floors. */
	public Floor[] floors;
	
	/** The elevators. */
	private Elevator[] elevators;
	
	/**  The Call Manager - it tracks calls for the elevator, analyzes them to answer questions and prioritize calls. */
	private CallManager callMgr;

	/** Dictates the amount of time it takes to offload passengers. */
	private int offLoadDelay;
	
	private int numBoarded; // indicates the number of passengers boarded in the board state

	private ArrayList<Passengers> temp;
	
	
	private boolean endBoardBecauseSkip = false;
	// Add any fields that you think you might need here...

	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param numElevators the num elevators
	 * @param logfile the logfile
	 */
	public Building(int numFloors, int numElevators,String logfile) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		passQ = new GenericQueue<Passengers>(PASSENGERS_QSIZE);
		passSuccess = new ArrayList<Passengers>();
		gaveUp = new ArrayList<Passengers>();
		Passengers.resetStaticID();		
		initializeBuildingLogger(logfile);
		// passDataFile is where you will write all the results for those passengers who successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log","PassData.csv"));
		
		// create the floors, call manager and the elevator arrays
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(FLOOR_QSIZE); 
		}
		callMgr = new CallManager(floors,NUM_FLOORS);
		elevators = new Elevator[NUM_ELEVATORS];
		//TODO: if you defined new fields, make sure to initialize them here
		
	}
	
	// TODO: Place all of your code HERE - state methods and helpers...
	/**
	 * Setting up the elevators (technically for this simulation there is only one elevator) with the respective parameters
	 */
	public void configElevators(int numFloors, int capacity, int floorTicks, int doorTicks, int passPerTick) {
		elevators[0] = new Elevator(numFloors, capacity, floorTicks, doorTicks, passPerTick); // configuring the Elevators
	}
	/**
	 * Returns a boolean value denoting whether the floor queue is empty or not, true being empty, false being not empty.
	 * 
	 * @return Used to check if the simulation has ended
	 */
	public boolean floorQueuesEmpty() {
		for (Floor f : floors) {
			if (!f.empty(Elevator.UP) || f.empty(Elevator.DOWN)) 
				return false;
		}
		return true;
	}
	/**
	 * Returns a boolean value whether adding the passenger to the queue was a success
	 * 
	 * @return successful add boolean
	 */
	public boolean addPassengersToQueue(int time, int numPass, int on, int dest, boolean polite, int waitTime) {
		return passQ.add(new Passengers(time, numPass, on, dest, polite, waitTime));
	}
	/**
	 * Checks the passenger queue at every tick
	 * 
	 * @param stepCnt
	 * At every tick, passengers that have to get on their initial floor as removed from the passQ and placed on the floor queues
	 */
	public void checkPassengerQueue(int stepCnt) {
		Passengers p;
		while (!passQ.isEmpty() && passQ.peek().getTime() == stepCnt) {
			p = passQ.poll();
			floors[p.getOnFloor()].add(p, p.getDirection());
			logCalls(stepCnt, p.getNumPass(), p.getOnFloor(), p.getDirection(), p.getId());
		}
	}
	
	// DO NOT CHANGE ANYTHING BELOW THIS LINE:
	/**
	 * Initialize building logger. Sets formating, file to log to, and
	 * turns the logger OFF by default
	 *
	 * @param logfile the file to log information to
	 */
	void initializeBuildingLogger(String logfile) {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 */
	// YOU WILL NEED TO CODE ANY MISSING METHODS IN THE APPROPRIATE CLASSES...
	public void updateElevator(int time) {
		for (Elevator lift: elevators) {
			if (elevatorStateChanged(lift))
				logElevatorStateChanged(time,lift.getPrevState(),lift.getCurrState(),lift.getPrevFloor(),lift.getCurrFloor());
			switch (lift.getCurrState()) {
				case Elevator.STOP: lift.updateCurrState(currStateStop(time,lift)); break;
				case Elevator.MVTOFLR: lift.updateCurrState(currStateMvToFlr(time,lift)); break;
				case Elevator.OPENDR: lift.updateCurrState(currStateOpenDr(time,lift)); break;
				case Elevator.OFFLD: lift.updateCurrState(currStateOffLd(time,lift)); break;
				case Elevator.BOARD: lift.updateCurrState(currStateBoard(time,lift)); break;
				case Elevator.CLOSEDR: lift.updateCurrState(currStateCloseDr(time,lift)); break;
				case Elevator.MV1FLR: lift.updateCurrState(currStateMv1Flr(time,lift)); break;
			}
		}
	}
	
	/**
	 * move the elevator to the next floor in the current direction.
	 * 
	 * @param time
	 * @param lift, the elevator
	 * @return int of the next state
	 */
	private int currStateMv1Flr(int time, Elevator lift) {
		
		lift.moveElevator();
		// Move the Elevator - this should be a method in Elevator - what should it do???
		if (lift.atNewFloor()) {
			int currFloor = lift.getCurrFloor(); // creating variables for the current floor and direction
			int dir = lift.getDirection();
			if (lift.passengersToGetOffOnFloor(currFloor)) { // method needs to be written in the Elevator class
				return Elevator.OPENDR;
			}
			if (passengersToBoard(currFloor, dir)) { // if there are passengers to board on this floor in the current direction, open the door
				return Elevator.OPENDR;
			}
			if (lift.isEmpty() && !callMgr.callsPendingInFloorsFromCorrespondingDir(currFloor, dir) && passengersToBoard(currFloor, -1*dir)) { // elevator is empty, no calls above if dir = up, no calls below if dir = down
				lift.changeDirection(); // change the direction
				return Elevator.OPENDR;
			}
			
		}
		return Elevator.MV1FLR;
	}

	/**
	 * Boards all waiting passengers in the current direction onto the elevator. 
	 * Time in this state is variable, based upon the number of boarders – but an added complexity is 
	 * that the wait time needs to continually be re-evaluated, 
	 * since new boarders can arrive while boarding is in progress. This means that the floor queue needs 
	 * to be examined for new arrivals every tick in this state.
	 * 
	 * @param time
	 * @param lift, the elevator
	 * @return int of the next state
	 */
	private int currStateBoard(int time, Elevator lift) {
		if (lift.elevatorStateChanged()) {
			endBoardBecauseSkip = false;
			numBoarded = 0;
		}
		int floor = lift.getCurrFloor(); int dir = lift.getDirection(); int numPass = 0;
		Passengers p; // a passenger reference that will be used locally for peeking
		while (!endBoardBecauseSkip && lift.isNotFull() && passengersToBoard(floor, dir)) {
			p = floors[floor].peek(dir);
			numPass = p.getNumPass();
			if (p.getTimeWillGiveUp() < time) {
				System.out.println("GAVE UP !!!");
				gaveUp.add(floors[floor].poll(dir));
				logGiveUp(time, numPass, p.getOnFloor(), p.getDirection(), p.getId());
			}
			else if (p.getNumPass() + lift.getPassengers() > lift.getCapacity()) { // else if there is not enough room to add the passengers
				logSkip(time, numPass, p.getOnFloor(), p.getDirection(), p.getId());
				p.setPolite(true); // adjust passenger if necessary
				endBoardBecauseSkip = true;
				break; // break the while loop
			}
			else {
				numBoarded += numPass;
				p.setBoardTime(time);
				logBoard(time, numPass, p.getOnFloor(), p.getDirection(), p.getId());
				lift.addPassengers(floors[floor].poll(dir));
			}
		}
		if ((numBoarded + 2 ) / 3 <= lift.getTimeInState()) return Elevator.CLOSEDR;
		return Elevator.BOARD;
	}
	
	/**
	 * Closes the elevator doors (decrements door state variable)
	 * 
	 * @param time
	 * @param lift, the elevator
	 * @return int of the next state
	 */	
	private int currStateCloseDr(int time, Elevator lift) {
		lift.decrementDoorState(); // closes the door partially
		Passengers p = floors[lift.getCurrFloor()].peek(lift.getDirection());
		if (p != null && !p.isPolite()) {
			p.setPolite(true);
			return Elevator.OPENDR;
		}
		if (lift.doorFullyClosed()) {
			if (lift.isEmpty()) {
				if (!callMgr.callsPending()) 
					return Elevator.STOP;
				if (callMgr.callsPendingInFloorsFromCorrespondingDir(lift.getCurrFloor(), lift.getDirection()))
					return Elevator.MV1FLR;
				if (callMgr.callsPending(lift.getCurrFloor(), lift.getDirection()))
					return Elevator.OPENDR;
				else {
					lift.changeDirection();
					if (callMgr.callsPending(lift.getCurrFloor(), lift.getDirection()))
						return Elevator.OPENDR;
					else
						return Elevator.MV1FLR;
				}
			}
			return Elevator.MV1FLR;
		}
		return Elevator.CLOSEDR;
	}
	
	/**
	 * This state models the time for passengers to leave the elevator. 
	 * The rate at which passengers leave the elevator is specified by the passPerTick configuration. 
	 * Time in this state is variable but can be determined upon entry into this state since the 
	 * number of passengers to exit is known and fixed.
	 * 
	 * @param time
	 * @param lift, the elevator
	 * @return int of the next state
	 */	
	private int currStateOffLd(int time, Elevator lift) {
		if (elevatorStateChanged(lift)) { // if just entered OFFLD state
			temp = lift.offload();
			offLoadDelay = (lift.numPassengersInArrayList(temp) + 2)/3;
			for (Passengers p : temp) {
				passSuccess.add(p);
				logArrival(time, p.getNumPass(), p.getDestFloor(), p.getId());
			}

		}
		if (lift.getTimeInState() == offLoadDelay) {
			if (passengersToBoard(lift.getCurrFloor(), lift.getDirection())) {
				return Elevator.BOARD;
			}
			if (lift.isEmpty() && !callMgr.callsPendingInFloorsFromCorrespondingDir(lift.getCurrFloor(), lift.getDirection()) && passengersToBoard(lift.getCurrFloor(),-1*lift.getDirection())) {
				lift.changeDirection();
				return Elevator.BOARD;
			}
			return Elevator.CLOSEDR;
		}
		return Elevator.OFFLD;
	}
	
	
	/**
	 * This state opens the elevator doors for off-loading or boarding passengers.
	 * 
	 * @param time
	 * @param lift, the elevator
	 * @return int of the next state
	 */	
	private int currStateOpenDr(int time, Elevator lift) {
		// TODO Auto-generated method stub
		lift.updateCurrFloor();
		lift.incrementDoorState(); // incrementing the doorState variable as the door opens
		if (!lift.doorFullyOpen()) { // if the door is not full open stay in this state
			return Elevator.OPENDR;
		}
		else {
			if (lift.passengersToGetOffOnFloor(lift.getCurrFloor())) {
				return Elevator.OFFLD;
			}
			return Elevator.BOARD; // this is the last cases, one of the cases should happen, hence no if statement is necessary
		}
		
	}

	/**
	 * This state moves the elevator to the target floor, ignoring any calls on intermediate floors, 
	 * even if they are in the same direction.
	 * 
	 * @param time
	 * @param lift, the elevator
	 * @return int of the next state
	 */	
	private int currStateMvToFlr(int time, Elevator lift) {
		// TODO Auto-generated method stub
		lift.moveElevator();
		if (lift.atTargetFloor()) {
			lift.setDirection(lift.getPostMoveToFloorDir());
			return Elevator.OPENDR;
		}
		return Elevator.MVTOFLR;
	}

	/**
	 * This state is entered when the Elevator is empty, the doors are closed and there are no calls on any floor.
	 * 
	 * @param time
	 * @param lift, the elevator
	 * @return int of the next state
	 */	
	private int currStateStop(int time, Elevator lift) {
		if (!callMgr.callsPending()) {
			return Elevator.STOP;
		}
		Passengers p = callMgr.prioritizePassengerCalls(lift.getCurrFloor());
		if (lift.getCurrFloor() == p.getOnFloor()) {
			lift.setDirection(p.getDirection());
			return Elevator.OPENDR;
		}
		lift.configureMoveToFloor(p);
		return Elevator.MVTOFLR;
	}
	
	/**
	 * Process passenger data. Do NOT change this - it simply dumps the 
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {
		
		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
		for (Elevator el:elevators)
			logElevatorConfig(el.getCapacity(),el.getTicksPerFloor(), el.getTicksDoorOpenClose(), el.getPassPerTick(), el.getCurrState(), el.getCurrFloor());
	}
	
	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}
	
	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";
		
		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}
	
	/**
	 * Dump passQ contents. Debug hook to view the contents of the passenger queue...
	 */
	public void dumpPassQ() {
		ListIterator<Passengers> passengers = passQ.getListIterator();
		if (passengers != null) {
			System.out.println("Passengers Queue:");
			while (passengers.hasNext()) {
				Passengers p = passengers.next();
				System.out.println(p);
			}
		}
	}
	/**
	 * Updates the call status in the Call Manager Class
	 */
	public void updateCallStatus() {
		callMgr.updateCallStatus();
	}
	
	/**
	 * Returns a boolean value depending on whether the param elevator state was changed. 
	 * 
	 * @param lift, the elevator object
	 * @return true if state changed
	 */
	
	private boolean elevatorStateChanged(Elevator lift) {
		// TODO Auto-generated method stub
		return lift.elevatorStateChanged();
	}
	
	/**
	 * Returns a boolean value depending on whether there are passengers to board on the floor and 
	 * the correct direction the elevator is moving in/towards.
	 * 
	 * @param floor
	 * @param dir
	 * @return If there are passengers to board on this floor in this direction
	 */
	public boolean passengersToBoard(int floor, int dir) {
		return !floors[floor].empty(dir);
	}
	
	
	/**
	 * Returns a boolean value depending on whether there are passengers to board the elevator on either direction.
	 * 
	 * @param floor
	 * @return if thee are passengers to board on this floor in any direction
	 */
	public boolean passengersToBoard(int floor) {
		return passengersToBoard(floor, Elevator.UP) || passengersToBoard(floor, Elevator.DOWN);
	}
	
	/**
	 * Returns the elevator object at the respective index
	 * 
	 * @param elevatorIndex
	 * @return The elevator object at the respective index
	 */
	public Elevator getElevator(int elevatorIndex) {
		return elevators[elevatorIndex % elevators.length];
	}
	
	/**
	 * Returns the string value of the queue string for the given elevator and the given floor
	 *
	 * @param floor
	 * @param direction
	 * @return The queue string for the given floor in that direction
	 */
	public String getQueueString(int floor, int direction) {
		return floors[floor].queueString(direction);
	}
	
	/**
	 * Returns a boolean value depending on whether the passenger queue array is empty.
	 * 
	 * @return true if the passenger queue is empty, false if not
	 */
	public boolean passengerQueueEmpty() {
		return passQ.isEmpty();
	}

	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick, int state, int floor) {
		LOGGER.info("CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1));
	}
		
	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		+"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}
	
	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		+" passID=" + id);						
	}
	
	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}
	
	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}
}
