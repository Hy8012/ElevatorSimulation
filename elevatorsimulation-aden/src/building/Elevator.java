package building;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/*
 * Class belongs to: Henry
 * Reviewed by: Joseph
 * Additional Comments: N/A
 */


// TODO: Auto-generated Javadoc
/**
 * The Class Elevator.
 *
 * @author This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targeting each floor...
 */
public class Elevator {
	
	/**  Constant for representing direction. */
	public static final int UP = 1;
	
	/** The Constant DOWN. */
	public static final int DOWN = -1;

	/**  Elevator State Variables - These are visible publicly. */
	public final static int STOP = 0;
	
	/** The Constant MVTOFLR. */
	public final static int MVTOFLR = 1;
	
	/** The Constant OPENDR. */
	public final static int OPENDR = 2;
	
	/** The Constant OFFLD. */
	public final static int OFFLD = 3;
	
	/** The Constant BOARD. */
	public final static int BOARD = 4;
	
	/** The Constant CLOSEDR. */
	public final static int CLOSEDR = 5;
	
	/** The Constant MV1FLR. */
	public final static int MV1FLR = 6;

	/** Default configuration parameters for the elevator. These should be
	 *  updated in the constructor.
	 */
	private int capacity = 15;				// The number of PEOPLE the elevator can hold
	
	/** The ticks per floor. */
	private int ticksPerFloor = 5;			// The time it takes the elevator to move between floors
	
	/** The ticks door open close. */
	private int ticksDoorOpenClose = 2;  	// The time it takes for doors to go from OPEN <=> CLOSED
	
	/** The pass per tick. */
	private int passPerTick = 3;            // The number of PEOPLE that can enter/exit the elevator per tick
	
	/**  Finite State Machine State Variables. */
	private int currState;		// current state
	
	/** The prev state. */
	private int prevState;      // prior state
	
	/** The prev floor. */
	private int prevFloor;      // prior floor
	
	/** The curr floor. */
	private int currFloor;      // current floor
	
	/** The direction. */
	private int direction;      // direction the Elevator is traveling in.
	
	/** The time in state. */
	private int timeInState = 1;    // represents the time in a given state
	                            // reset on state entry, used to determine if
	                            // state has completed or if floor has changed
	                            // *not* used in all states 
	
	/** The door state. */
	private int doorState;      // used to model the state of the doors - OPEN, CLOSED
	                            
                            	/** The passengers. */
                            	// or moving
	private int passengers;  	// the number of people in the elevator
	
	/** The pass by floor. */
	private ArrayList<Passengers>[] passByFloor;  // Passengers to exit on the corresponding floor

	/** The move to floor. */
	private int moveToFloor;	// When exiting the STOP state, this is the floor to move to without
	                            // stopping.
	
	/** The post move to floor dir. */
    private int postMoveToFloorDir; // This is the direction that the elevator will travel AFTER reaching
	                                // the moveToFloor in MVTOFLR state.
	/**
	 * Instantiates a new elevator.
	 * @param numFloors the num floors
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the pass per tick
	 */
                                	@SuppressWarnings("unchecked")
	public Elevator(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {
        this.prevState = STOP;
		this.currState = STOP;
		this.timeInState = 0;
		this.direction = 1;
		this.currFloor = 0;
		passByFloor = new ArrayList[numFloors];
		
		for (int i = 0; i < numFloors; i++) 
			passByFloor[i] = new ArrayList<Passengers>(); 

		//TODO: Finish this constructor, adding configuration initialiation and
		//      initialization of any other private fields, etc.
		this.capacity = capacity;
		this.ticksPerFloor = floorTicks;
		this.ticksDoorOpenClose = doorTicks;
		this.passPerTick = passPerTick;
	}
	
	//TODO: Add Getter/Setters and any methods that you deem are required. Examples 
	//      include:
	//      1) moving the elevator
	//      2) closing the doors
	//      3) opening the doors
	//      and so on...
	
	/**
	 * Updates the current state of the elevator.
	 *
	 * @param state of the elevator that needs to be updated
	 */
	public void updateCurrState(int state) {
		this.prevState = this.currState;
		this.currState = state;
		if(this.prevState != this.currState) {
			timeInState = 1;
		} else if(this.prevState == this.currState) {
			timeInState++;
		}
	}
	
	/**
	 * Moves the elevator in the correct direction.
	 */
	public void moveElevator() {
		prevFloor = currFloor; //CHANGED-CODE
		if((timeInState % ticksPerFloor) == 0) {
			//prevFloor = currFloor; //CHANGED-CODE (removed)
			
			currFloor = currFloor + direction;
		}
	}
	
	/**
	 * Offload corresponding method for the Elevator class.
	 *
	 * @return the array list of passengers for the correct floor
	 */
	public ArrayList<Passengers> offload() {
		ArrayList<Passengers> temp = new ArrayList<Passengers>();
		if(passByFloor[currFloor].size() > 0) {
			passengers -= numPassengersInArrayList(passByFloor[currFloor]);
			for (Passengers p : passByFloor[currFloor]) {
				temp.add(p);
			}
			passByFloor[currFloor].clear();
			return temp;
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a int value of the number of passengers in the array list.
	 *
	 * @param arr the array
	 * @return int, the number of passengers in the array list
	 */
	public int numPassengersInArrayList(ArrayList<Passengers> arr) {
		int ans = 0;
		for (Passengers p : arr) {
			ans += p.getNumPass();
		}
		return ans;
	}
	
	/**
	 * Configures the variable which determines the floor to move to.
	 *
	 * @param p the passenger
	 */
	public void configureMoveToFloor(Passengers p) {
		moveToFloor = p.getOnFloor();
		if (moveToFloor < currFloor) {
			direction = Elevator.DOWN;
		}
		else {
			direction = Elevator.UP;
		}
		postMoveToFloorDir = p.getDirection();
	}
	
	/**
	 * Checks if the elevator is moving.
	 *
	 * @return true, if is moving
	 */
	public boolean isMoving() {
		return ((timeInState % ticksPerFloor) != 0);
	}
			
	/**
	 * Checks if the elevator is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		if(passengers == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * // Returns a boolean value checking whether the elevator is full or not.
	 *
	 * @return true, if is full
	 */
	public boolean isNotFull() {
		if(passengers <= capacity) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a boolean value, true being the elevator has arrived at the target floor, and false being that the elevator has yet or not arrived.
	 *
	 * @return true if the elevator has reached its target floor in move to floor or false if not
	 */
	public boolean atTargetFloor() {
		return (currFloor == moveToFloor);
	}
	
	/**
	 * Returns a boolean if there are passengers to get off at this specific floor.
	 *
	 * @param floor the floor
	 * @return true, if successful
	 */
	public boolean passengersToGetOffOnFloor(int floor) {
		for (Passengers p: passByFloor[floor]) {
			if (p.getDestFloor() == floor) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the elevator is at a new floor.
	 *
	 * @return true, if the elevator has arrived at a new floor.
	 */
	public boolean atNewFloor() {
		if(prevFloor != currFloor) {
			return true;
		}
		return false;
	}
	
	/**
	 * Updates prevFloor to currFloor when called
	 */
	public void updateCurrFloor() {
		prevFloor = currFloor;
	}
	
	/**
	 * Returns a boolean if the timeinState is equal to the inputted offload delay calculated in building class.
	 *
	 * @param offloadDelay the offload delay
	 * @return true, if successful
	 */
	public boolean timeInStateEqualsOffloadDelay(int offloadDelay) {
		return (timeInState == offloadDelay);
	}
	
	/**
	 * Returns a boolean value whether the door is fully open or not. True being the food is fully open, false being the door is not.
	 *
	 * @return true if the door is fully open, false if not
	 */
	public boolean doorFullyOpen() {
		return (doorState == ticksDoorOpenClose);
	}
	
	/**
	 * Returns a boolean value whether the door is fully closed or not. True being the food is fully closed, false being the door is not.
	 *
	 * @return true if the door is fully closed, false if not
	 */
	public boolean doorFullyClosed() {
		return (doorState == 0);
	}
	
	/*
	 * Changes the direction of the elevator
	 */
	public void changeDirection() {
		direction *= -1;
	}
	
	
	/**
	 * Adds the passenger to passByFloor
	 *
	 * @param p the p
	 */
	public void addPassengers(Passengers p) {
		passengers += p.getNumPass();
		passByFloor[p.getDestFloor()].add(p);
	}
	
	/**
	 * Returns a boolean value based on whether the elevator state was updated properly
	 *
	 * @return If the elevator state has changed
	 */
	public boolean elevatorStateChanged() {
		return (prevState != currState) || (prevFloor != currFloor);
	}

	
	
	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	
	/**
	 * Sets the capacity.
	 *
	 * @param capacity the new capacity
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Gets the ticks per floor.
	 *
	 * @return the ticks per floor
	 */
	public int getTicksPerFloor() {
		return ticksPerFloor;
	}

	/**
	 * Sets the ticks per floor.
	 *
	 * @param ticksPerFloor the new ticks per floor
	 */
	public void setTicksPerFloor(int ticksPerFloor) {
		this.ticksPerFloor = ticksPerFloor;
	}

	/**
	 * Gets the ticks door open close.
	 *
	 * @return the ticks door open close
	 */
	public int getTicksDoorOpenClose() {
		return ticksDoorOpenClose;
	}

	/**
	 * Sets the ticks door open close.
	 *
	 * @param ticksDoorOpenClose the new ticks door open close
	 */
	public void setTicksDoorOpenClose(int ticksDoorOpenClose) {
		this.ticksDoorOpenClose = ticksDoorOpenClose;
	}

	/**
	 * Gets the pass per tick.
	 *
	 * @return the pass per tick
	 */
	public int getPassPerTick() {
		return passPerTick;
	}

	/**
	 * Sets the pass per tick.
	 *
	 * @param passPerTick the new pass per tick
	 */
	public void setPassPerTick(int passPerTick) {
		this.passPerTick = passPerTick;
	}

	/**
	 * Gets the curr state.
	 *
	 * @return the curr state
	 */
	public int getCurrState() {
		return currState;
	}

	/**
	 * Sets the curr state.
	 *
	 * @param currState the new curr state
	 */
	public void setCurrState(int currState) {
		this.currState = currState;
	}

	/**
	 * Gets the prev state.
	 *
	 * @return the prev state
	 */
	public int getPrevState() {
		return prevState;
	}

	/**
	 * Sets the prev state.
	 *
	 * @param prevState the new prev state
	 */
	public void setPrevState(int prevState) {
		this.prevState = prevState;
	}

	/**
	 * Gets the prev floor.
	 *
	 * @return the prev floor
	 */
	public int getPrevFloor() {
		return prevFloor;
	}

	/**
	 * Sets the prev floor.
	 *
	 * @param prevFloor the new prev floor
	 */
	public void setPrevFloor(int prevFloor) {
		this.prevFloor = prevFloor;
	}

	/**
	 * Gets the curr floor.
	 *
	 * @return the curr floor
	 */
	public int getCurrFloor() {
		return currFloor;
	}

	/**
	 * Sets the curr floor.
	 *
	 * @param currFloor the new curr floor
	 */
	public void setCurrFloor(int currFloor) {
		this.currFloor = currFloor;
	}

	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * Gets the opposite direction.
	 *
	 * @return the opposite direction
	 */
	public int getOppositeDirection() {
		return -1 * direction;
	}
	
	/**
	 * Sets the direction.
	 *
	 * @param direction the new direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Gets the time in state.
	 *
	 * @return the time in state
	 */
	public int getTimeInState() {
		return timeInState;
	}

	/**
	 * Sets the time in state.
	 *
	 * @param timeInState the new time in state
	 */
	public void setTimeInState(int timeInState) {
		this.timeInState = timeInState;
	}

	/**
	 * Gets the door state.
	 *
	 * @return the door state
	 */
	public int getDoorState() {
		return doorState;
	}

	/**
	 * Sets the door state.
	 *
	 * @param doorState the new door state
	 */
	public void setDoorState(int doorState) {
		this.doorState = doorState;
	}
	
	/**
	 * Increment door state.
	 */
	public void incrementDoorState() {
		doorState++;
	}
	
	/**
	 * Decrement door state.
	 */
	public void decrementDoorState() {
		doorState--;
	}

	/**
	 * Gets the passengers.
	 *
	 * @return the passengers
	 */
	public int getPassengers() {
		return passengers;
	}

	/**
	 * Sets the passengers.
	 *
	 * @param passengers the new passengers
	 */
	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}
	
	/**
	 * Gets the pass by floor.
	 *
	 * @return the pass by floor
	 */
	public ArrayList<Passengers>[] getPassByFloor() {
		return passByFloor;
	}

	/**
	 * Sets the pass by floor.
	 *
	 * @param passByFloor the new pass by floor
	 */
	public void setPassByFloor(ArrayList<Passengers>[] passByFloor) {
		this.passByFloor = passByFloor;
	}

	/**
	 * Gets the move to floor.
	 *
	 * @return the move to floor
	 */
	public int getMoveToFloor() {
		return moveToFloor;
	}

	/**
	 * Sets the move to floor.
	 *
	 * @param moveToFloor the new move to floor
	 */
	public void setMoveToFloor(int moveToFloor) {
		this.moveToFloor = moveToFloor;
	}

	/**
	 * Gets the post move to floor dir.
	 *
	 * @return the post move to floor dir
	 */
	public int getPostMoveToFloorDir() {
		return postMoveToFloorDir;
	}

	/**
	 * Sets the post move to floor dir.
	 *
	 * @param postMoveToFloorDir the new post move to floor dir
	 */
	public void setPostMoveToFloorDir(int postMoveToFloorDir) {
		this.postMoveToFloorDir = postMoveToFloorDir;
	}

	/**
	 * Gets the stop.
	 *
	 * @return the stop
	 */
	public static int getStop() {
		return STOP;
	}

	/**
	 * Gets the mvtoflr.
	 *
	 * @return the mvtoflr
	 */
	public static int getMvtoflr() {
		return MVTOFLR;
	}

	/**
	 * Gets the opendr.
	 *
	 * @return the opendr
	 */
	public static int getOpendr() {
		return OPENDR;
	}

	/**
	 * Gets the offld.
	 *
	 * @return the offld
	 */
	public static int getOffld() {
		return OFFLD;
	}

	/**
	 * Gets the board.
	 *
	 * @return the board
	 */
	public static int getBoard() {
		return BOARD;
	}

	/**
	 * Gets the closedr.
	 *
	 * @return the closedr
	 */
	public static int getClosedr() {
		return CLOSEDR;
	}

	/**
	 * Gets the mv 1 flr.
	 *
	 * @return the mv 1 flr
	 */
	public static int getMv1flr() {
		return MV1FLR;
	}
}
