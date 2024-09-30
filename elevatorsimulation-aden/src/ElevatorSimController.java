import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import building.Building;
import building.Elevator;
import myfileio.MyFileIO;

// TODO: Auto-generated Javadoc
/*
 * Class belongs to: Joseph
 * Reviewed by: Nikash
 * Additional Comments: n/a
 */

/**
 * The Class ElevatorSimController.
 * 
 * • Configures the simulation as part of the CONSTRUCTOR!
 * 		– Reads config file to configure
 * 			• building (num floors & elevators) - pass to GUI and building
 *			• Elevator configuration (capacity, vertical speed, door speed, passenger load rate) – all in terms of ticks...
 *			• the passenger CSV file to simulate
 *		– Reads passenger CSV file and provides passenger data to Building
 *		– Both have been provided for you already
 * • Simulation Control:
 *		– Keeps global time – passes back to GUI and down to Building
 *		– Each tick:
 *			• Building checks to see if passengers added to Floor Queues
 *			• Building updates Elevator
 *			• If visible state changed – elevator or floor, notify GUI
 *		– Pass enable logging request downstream
 */
public class ElevatorSimController {
	
	/**  Constant to specify the configuration file for the simulation. */
	private static final String SIM_CONFIG = "ElevatorSimConfig.csv";
	
	/**  Constant to make the Passenger queue contents visible after initialization. */
	private boolean PASSQ_DEBUG=false;
	
	/** The gui. */
	private ElevatorSimulation gui;
	
	/** The building. */
	private Building building;
	
	/** The fio. */
	private MyFileIO fio;

	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num elevators. */
	private final int NUM_ELEVATORS;
	
	/** The num floors. */
	private int numFloors;
	
	/** The num elevators. */
	private int numElevators;
	
	/** The capacity. */
	private int capacity;
	
	/** The floor ticks. */
	private int floorTicks;
	
	/** The door ticks. */
	private int doorTicks;
	
	/** The pass per tick. */
	private int passPerTick;
	
	/** The testfile. */
	private String testfile;
	
	/** The logfile. */
	private String logfile;
	
	/** The step cnt. */
	private int stepCnt = 0;
	
	/** The end sim. */
	private boolean endSim = false;
	

	/**
	 * Instantiates a new elevator sim controller. 
	 * Reads the configuration file to configure the building and
	 * the elevator characteristics and also select the test
	 * to run. Reads the passenger data for the test to run to
	 * initialize the passenger queue in building...
	 *
	 * @param gui the gui
	 */
	public ElevatorSimController(ElevatorSimulation gui) {
		if (gui != null) {
			this.gui = gui;
		}
		fio = new MyFileIO();
		// IMPORTANT: DO NOT CHANGE THE NEXT LINE!!! Update the config file itself
		// (ElevatorSimConfig.csv) to change the configuration or test being run.
		configSimulation(SIM_CONFIG);
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		logfile = testfile.replaceAll(".csv", ".log");
		building = new Building(NUM_FLOORS,NUM_ELEVATORS,logfile);
		building.configElevators(numFloors, capacity, floorTicks, doorTicks, passPerTick);
		initializePassengerData(testfile);
	}
	
	/**
	 * Config simulation. Reads the filename, and parses the
	 * parameters.
	 *
	 * @param filename the filename
	 */
	private void configSimulation(String filename) {
		File configFile = fio.getFileHandle(filename);
		try ( BufferedReader br = fio.openBufferedReader(configFile)) {
			String line;
			while ((line = br.readLine())!= null) {
				parseElevatorConfigData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the elevator simulation config file to configure the simulation:
	 * number of floors and elevators, the actual test file to run, and the
	 * elevator characteristics.
	 *
	 * @param line the line
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseElevatorConfigData(String line) throws IOException {
		String[] values = line.split(",");
		if (values[0].equals("numFloors")) {
			numFloors = Integer.parseInt(values[1]);
		} else if (values[0].equals("numElevators")) {
			numElevators = Integer.parseInt(values[1]);
		} else if (values[0].equals("passCSV")) {
			testfile = values[1];
		} else if (values[0].equals("capacity")) {
			capacity = Integer.parseInt(values[1]);
		} else if (values[0].equals("floorTicks")) {
			floorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("doorTicks")) {
			doorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("passPerTick")) {
			passPerTick = Integer.parseInt(values[1]);
		}
	}
	
	/**
	 * Initialize passenger data. Reads the supplied filename,
	 * and for each passenger group, identifies the pertinent information
	 * and adds it to the passengers queue in Building...
	 *
	 * @param filename the filename
	 */
	private void initializePassengerData(String filename) {
		boolean firstLine = true;
		File passInput = fio.getFileHandle(filename);
		try (BufferedReader br = fio.openBufferedReader(passInput)) {
			String line;
			while ((line = br.readLine())!= null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				parsePassengerData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
		if (PASSQ_DEBUG) building.dumpPassQ();
	}	
	
	/**
	 * Parses the line of passenger data into tokens, and 
	 * passes those values to the building to be added to the
	 * passenger queue.
	 *
	 * @param line the line of passenger input data
	 */
	private void parsePassengerData(String line) {
		int time=0, numPass=0,fromFloor=0, toFloor=0;
		boolean polite = true;
		int wait = 1000;
		String[] values = line.split(",");
		for (int i = 0; i < values.length; i++) {
			switch (i) {
				case 0 : time      = Integer.parseInt(values[i]); break;
				case 1 : numPass   = Integer.parseInt(values[i]); break;
				case 2 : fromFloor   = Integer.parseInt(values[i]); break;
				case 3 : toFloor  = Integer.parseInt(values[i]); break;
				case 5 : wait      = Integer.parseInt(values[i]); break;
				case 4 : polite = "TRUE".equalsIgnoreCase(values[i]); break;
			}
		}
		building.addPassengersToQueue(time,numPass,fromFloor,toFloor,polite,wait);	
	}
	
	/**
	 * Enable logging. A pass-through from the GUI to building
	 */
	public void enableLogging() {
		building.enableLogging();
	}
		
	/**
	 * Gets the num floors.
	 *
	 * @return the num floors
	 */
	public int getNumFloors() {
		return numFloors;
	}

	/**
	 * Gets the num elevators.
	 *
	 * @return the num elevators
	 */
	public int getNumElevators() {
		return numElevators;
	}
	
	/**
	 * Gets the step count.
	 *
	 * @return the step count
	 */
	public int getStepCount() {
		return stepCnt;
	}


	/**
	 * Returns the floor the elevator is on.
	 * Currently assumes the building only has one elevator! 
	 *
	 * @param elevatorIndex the elevator index
	 * @return the current floor
	 */
	public int getCurrentFloor(int elevatorIndex) {
		Elevator e = building.getElevator(elevatorIndex);
		return e.getCurrFloor();
	}
	
	
	/**
	 * Returns direction of an elevator.
	 *
	 * @param elevatorIndex the elevator index
	 * @return the current direction
	 */
	public int getCurrentDirection(int elevatorIndex) {
		Elevator e = building.getElevator(elevatorIndex);
		return e.getDirection();
	}
	
	
	/**
	 * Returns the number of passengers in an elevator.
	 *
	 * @param elevatorIndex the elevator index
	 * @return the current passengers
	 */
	public int getCurrentPassengers(int elevatorIndex) {
		Elevator e = building.getElevator(elevatorIndex);
		return e.getPassengers();
	}
	
	
	/**
	 * Returns the string value of the queue taking into consideration both the floor and direction of the elevator.
	 *
	 * @param floor the floor
	 * @param direction the direction
	 * @return the queue string
	 */
	public String getQueueString(int floor, int direction) {
		return building.getQueueString(floor, direction);
	}
	
	
	/**
	 * Returns the test name (which is the test file csv).
	 *
	 * @return the test name
	 */
	public String getTestName() {
		return testfile;
	}


	/**
	 * Returns true if the simulation has ended.
	 * (all passengers have been processed and elevators are all in the STOP state)
	 * @return true if the simulation has ended
	 */
	public boolean checkIfSimulationHasEnded() {		
		for (int i = 0; i < NUM_ELEVATORS; i++) {
			if (building.getElevator(i).getPrevState() != Elevator.STOP)
				return false;
		}
		return building.passengerQueueEmpty();
	}
	
 	/**
	 * Step sim. See the comments below for the functionality you
	 * must implement......
	 */
	public void stepSim() {
 		// DO NOT MOVE THIS - YOU MUST INCREMENT TIME FIRST!
		stepCnt++;
		// If simulation is not completed (not all passengers have been processed
		// or elevator(s) are not all in STOP state), then
		if (!checkIfSimulationHasEnded()) {
			building.checkPassengerQueue(stepCnt); // 1) check for arrival of any new passengers
			building.updateCallStatus(); // important that this is here

			building.updateElevator(stepCnt); // 2) update the elevator			
			if (gui != null) {
				gui.updateGUI(); // 3) update the GUI 
			}
		} else {
			building.updateElevator(stepCnt); // Update the elevator one last time just to properly log the stop state.			
			if (gui != null) { // We don't need to do any of the below if there isn't a GUI (the JUnit tests handle it otherwise)
				// 1) update the GUI
				gui.updateGUI(); 
				// 4) send endSimulation to the GUI to stop ticks.
				gui.endSimulation();
			}
			// 2) close the logs
			building.closeLogs(stepCnt);
			// 3) process passenger results
			building.processPassengerData();		}
	}
	
	

	

	
	/**
	 * Gets the building. ONLY USED FOR JUNIT TESTING - YOUR GUI SHOULD NOT ACCESS THIS!.
	 *
	 * @return the building
	 */
	Building getBuilding() {
		return building;
	}
	
	/**
	 * Gets the elevator state as a display string
	 * @param elevatorIndex
	 * @return String
	 */
	public String getStateString(int elevatorIndex) {
		Elevator e = building.getElevator(elevatorIndex);
		switch (e.getCurrState()) {
			case Elevator.STOP:
				return "STOP";
			case Elevator.MVTOFLR:
				return "MVTOFLR";
			case Elevator.OPENDR:
				return "OPENDR";
			case Elevator.OFFLD:
				return "OFFLD";
			case Elevator.BOARD:
				return "BOARD";
			case Elevator.CLOSEDR:
				return "CLOSEDR";
			case Elevator.MV1FLR:
				return "MV1FLR";
		}
		return "";
	}

}
