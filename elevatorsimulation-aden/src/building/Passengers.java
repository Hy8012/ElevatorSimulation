package building;

// TODO: Auto-generated Javadoc
/*
 * Class belongs to: Henry
 * Reviewed by: Joseph
 * Additional Comments:
 */


// TODO: Auto-generated Javadoc
/**
 * The Class Passengers. Represents a GROUP of passengers that are 
 * traveling together from one floor to another. Tracks information that 
 * can be used to analyze Elevator performance.
 */
public class Passengers {
	
	/**  Constant for representing direction. */
	private static final int UP = 1;
	
	/** The Constant DOWN. */
	private static final int DOWN = -1;
	
	/**  ID represents the NEXT available id for the passenger group. */
	private static int ID=0;

	/** id is the unique ID assigned to each Passenger during construction.
	 *  After assignment, static ID must be incremented.
	 */
	private int id;
	
	/** These fields will be passed into the constructor by the Building.
	 *  This data will come from the .csv file read by the SimController
	 */
	private int time;         // the time that the Passenger will call the elevator
	
	/** The num pass. */
	private int numPass;      // the number of passengers in this group
	
	/** The on floor. */
	private int onFloor;      // the floor that the Passenger will appear on
	
	/** The dest floor. */
	private int destFloor;	  // the floor that the Passenger will get off on
	
	/** The polite. */
	private boolean polite;   // will the Passenger let the doors close?
	
	/** The wait time. */
	private int waitTime;     // the amount of time that the Passenger will wait for the
	                          // Elevator
	
	/** These values will be calculated during construction.
	 */
	private int direction;      // The direction that the Passenger is going
	
	/** The time will give up. */
	private int timeWillGiveUp; // The calculated time when the Passenger will give up
	
	/** These values will actually be set during execution. Initialized to -1 */
	private int boardTime=-1;
	
	/** The time arrived. */
	private int timeArrived=-1;

	/**
	 * Instantiates a new passengers.
	 *
	 * @param time the time
	 * @param numPass the number of people in this Passenger
	 * @param on the floor that the Passenger calls the elevator from
	 * @param dest the floor that the Passenger is going to
	 * @param polite - are the passengers polite?
	 * @param waitTime the amount of time that the passenger will wait before giving up
	 */
	public Passengers(int time, int numPass, int on, int dest, boolean polite, int waitTime) {
	// TODO: Write the constructor for this class
	//       Remember to appropriately adjust the onFloor and destFloor to account  
	//       to convert from American to European numbering...
		
		this.time = time;
		this.numPass = numPass;
		this.onFloor = on - 1;
		this.destFloor = dest - 1;
		this.polite = polite;
		this.waitTime = waitTime;
		this.timeWillGiveUp = time + waitTime;
		this.id = ID;
		if (onFloor < destFloor) {
			direction = UP;
		}
		else {
			direction = DOWN;
		}
		ID++;
		
	}
	
	// TODO: Write any required getters/setters for this class

	// 
	/**
	 * Reset static ID. 
	 * This method MUST be called during the building constructor BEFORE
	 * reading the configuration files. This is to provide consistency in the
	 * Passenger ID's during JUnit testing.
	 */
	static void resetStaticID() {
		ID = 0;
	}

	/**
	 * toString - returns the formatted string for this class.
	 *
	 * @return the
	 */
	@Override
	public String toString() {
		return("ID="+id+"   Time="+time+"   NumPass="+numPass+"   From="+(onFloor+1)+"   To="+(destFloor+1)+"   Polite="+polite+"   Wait="+waitTime);
	}

	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public static int getID() {
		return ID;
	}

	/**
	 * Sets the id.
	 *
	 * @param iD the new id
	 */
	public static void setID(int iD) {
		ID = iD;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * Gets the num pass.
	 *
	 * @return the num pass
	 */
	public int getNumPass() {
		return numPass;
	}

	/**
	 * Sets the num pass.
	 *
	 * @param numPass the new num pass
	 */
	public void setNumPass(int numPass) {
		this.numPass = numPass;
	}

	/**
	 * Gets the on floor.
	 *
	 * @return the on floor
	 */
	public int getOnFloor() {
		return onFloor;
	}

	/**
	 * Sets the on floor.
	 *
	 * @param onFloor the new on floor
	 */
	public void setOnFloor(int onFloor) {
		this.onFloor = onFloor;
	}
	
	/**
	 * Gets the dest floor.
	 *
	 * @return the dest floor
	 */
	public int getDestFloor() {
		return destFloor;
	}

	/**
	 * Sets the dest floor.
	 *
	 * @param destFloor the new dest floor
	 */
	public void setDestFloor(int destFloor) {
		this.destFloor = destFloor;
	}

	/**
	 * Checks if is polite.
	 *
	 * @return true, if is polite
	 */
	public boolean isPolite() {
		return polite;
	}

	/**
	 * Sets the polite.
	 *
	 * @param polite the new polite
	 */
	public void setPolite(boolean polite) {
		this.polite = polite;
	}

	/**
	 * Gets the wait time.
	 *
	 * @return the wait time
	 */
	public int getWaitTime() {
		return waitTime;
	}

	/**
	 * Sets the wait time.
	 *
	 * @param waitTime the new wait time
	 */
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
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
	 * Sets the direction.
	 *
	 * @param direction the new direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Gets the time will give up.
	 *
	 * @return the time will give up
	 */
	public int getTimeWillGiveUp() {
		return timeWillGiveUp;
	}

	/**
	 * Sets the time will give up.
	 *
	 * @param timeWillGiveUp the new time will give up
	 */
	public void setTimeWillGiveUp(int timeWillGiveUp) {
		this.timeWillGiveUp = timeWillGiveUp;
	}

	/**
	 * Gets the board time.
	 *
	 * @return the board time
	 */
	public int getBoardTime() {
		return boardTime;
	}

	/**
	 * Sets the board time.
	 *
	 * @param boardTime the new board time
	 */
	public void setBoardTime(int boardTime) {
		this.boardTime = boardTime;
	}

	/**
	 * Gets the time arrived.
	 *
	 * @return the time arrived
	 */
	public int getTimeArrived() {
		return timeArrived;
	}

	/**
	 * Sets the time arrived.
	 *
	 * @param timeArrived the new time arrived
	 */
	public void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}

	/**
	 * Gets the up.
	 *
	 * @return the up
	 */
	public static int getUp() {
		return UP;
	}

	/**
	 * Gets the down.
	 *
	 * @return the down
	 */
	public static int getDown() {
		return DOWN;
	}

}
