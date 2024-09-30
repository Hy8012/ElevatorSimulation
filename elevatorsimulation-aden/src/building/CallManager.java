package building;

// TODO: Auto-generated Javadoc
/*
 * Class belongs to: Nikash
 * Reviewed by: Henry
 * Additional Comments: n/a
 */

/**
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The up calls array indicates whether or not there is a up call on each floor. */
	private boolean[] upCalls;
	
	/** The down calls array indicates whether or not there is a down call on each floor. */
	private boolean[] downCalls;
	
	/**  The up call pending - true if any up calls exist. */
	private boolean upCallPending;
	
	/**  The down call pending - true if any down calls exit. */
	private boolean downCallPending;
	
	//TODO: Add any additional fields here..
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
		
		//TODO: Initialize any added fields here
	}
	
	/**
	 * Update call status. This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 */
	public void updateCallStatus() {
		upCallPending = false;
		downCallPending = false;
		for (int i = 0; i < NUM_FLOORS; ++i) {
			upCalls[i] = !floors[i].empty(Elevator.UP);
			upCallPending = upCallPending || upCalls[i];
			downCalls[i] = !floors[i].empty(Elevator.DOWN);
			downCallPending = downCallPending || downCalls[i];
		}
	}

	
	/**
	 * Gets the up calls.
	 *
	 * @return the up calls
	 */
	public boolean[] getUpCalls() {
		return upCalls;
	}

	/**
	 * Sets the up calls.
	 *
	 * @param upCalls the new up calls
	 */
	public void setUpCalls(boolean[] upCalls) {
		this.upCalls = upCalls;
	}

	/**
	 * Gets the down calls.
	 *
	 * @return the down calls
	 */
	public boolean[] getDownCalls() {
		return downCalls;
	}

	/**
	 * Sets the down calls.
	 *
	 * @param downCalls the new down calls
	 */
	public void setDownCalls(boolean[] downCalls) {
		this.downCalls = downCalls;
	}

	/**
	 * Prioritizes the most important calls from unique floors, and depending on the state of the elevator.
	 *
	 * @param floor the floor
	 * @return the passengers
	 */
	Passengers prioritizePassengerCalls(int floor) {
		//TODO: Write this method based upon prioritization from STOP...
		if (callsPending(floor)) {
			if (numCallsPending(floor, Elevator.UP) > 0 && numCallsPending(floor, Elevator.DOWN) == 0)
				return floors[floor].peek(Elevator.UP);
			else if (numCallsPending(floor, Elevator.UP) == 0 && numCallsPending(floor, Elevator.DOWN) > 0)
				return floors[floor].peek(Elevator.DOWN);
			else if (numUpAboveOrDownBelow(floor, Elevator.UP) >= numUpAboveOrDownBelow(floor, Elevator.DOWN)) {
				return floors[floor].peek(Elevator.UP);
			}
			else {
				return floors[floor].peek(Elevator.DOWN);
			}
		}
		if (numCallsPending(Elevator.UP) > numCallsPending(Elevator.DOWN)) {
			return floors[extremeFloorWithCall(Elevator.UP)].peek(Elevator.UP);
		}
		if (numCallsPending(Elevator.UP) < numCallsPending(Elevator.DOWN)) {
			return floors[extremeFloorWithCall(Elevator.DOWN)].peek(Elevator.DOWN);
		}
		if (numCallsPending(Elevator.UP) == numCallsPending(Elevator.DOWN)) {
			if (Math.abs(floor - extremeFloorWithCall(Elevator.DOWN)) < Math.abs(floor - extremeFloorWithCall(Elevator.UP)))
				return floors[extremeFloorWithCall(Elevator.DOWN)].peek(Elevator.DOWN);

			else
				return floors[extremeFloorWithCall(Elevator.UP)].peek(Elevator.UP);
		}
		
		return null;
	}

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...
	
	/**
	 * Returns a boolean value if there are any calls (up/down) on the inputted floor (param).
	 *
	 * @param floor the floor
	 * @return are there any calls pending on this floor
	 */
	public boolean callsPending(int floor) { //
		return upCalls[floor] || downCalls[floor];
	}
	
	/**
	 * Returns a boolean value depending on if there are any calls in the inputted floor and direction
	 *
	 * @param floor the floor
	 * @param dir the dir
	 * @return Are there any calls pending in this floor in the current direction
	 */
	public boolean callsPending(int floor, int dir) {
		if (dir > 0) {
			return upCalls[floor];
		}
		return downCalls[floor];
	}
	
	/**
	 * Returns a boolean value depending on whether there are any calls pending on any of the floors
	 *
	 * @return if there are ANY calls pending
	 */
	public boolean callsPending() {
		return upCallPending || downCallPending;
	}
	
	/**
	 * Returns a boolean value whether there are any up calls pending, depending on the current floor of the elevator.
	 *
	 * @return if there are any up calls pending
	 */
	public boolean upCallPending() {
		return upCallPending;
	}
	
	/**
	 * Returns a boolean value whether there are any down calls pending, depending on the current floor of the elevator.
	 *
	 * @return if there are any down calls pending
	 */
	public boolean downCallPending() {
		return downCallPending;
	}
	
	/**
	 * Returns an integer value depending on the number of calls pending on a given floor and direction.
	 *
	 * @param floor the floor
	 * @param dir the dir
	 * @return The number of calls pending in a given floor in a given direction
	 */
	public int numCallsPending(int floor, int dir) {
		return floors[floor].size(dir);
	}
	
	/**
	 * Returns an integer value depending on the number of calls pending in a given direction not exclusive to one floor.
	 *
	 * @param dir the dir
	 * @return the number of calls pending in a given direction across all floors
	 */
	public int numCallsPending(int dir) {
		int ans = 0;
		for (Floor f : floors) {
			ans += f.size(dir);
		}
		return ans;
	}
	
	/**
	 * Returns an integer value depending on the number of calls down from the highest floor, and/or calls up from the lowest floor.
	 *
	 * @param dir the dir
	 * @return Either the lowest floor with an up call or the highest floor with a down call
	 */
	private int extremeFloorWithCall(int dir) {
		if (dir > 0) {
			for (int i = 0; i < upCalls.length; ++i) {
				if (upCalls[i]) {
					return i;
				}
			}
		}
		for (int i = downCalls.length - 1; i > -1; i--) {
			if (downCalls[i]) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Returns a boolean value checking whether there are calls pending from floors that are in the current direction the elevator is moving in.
	 *
	 * @param floor the floor
	 * @param dir the dir
	 * @return if there are calls from floors above if dir = up, floors below if dir = down
	 */
	public boolean callsPendingInFloorsFromCorrespondingDir(int floor, int dir) {
		if (dir > 0) {
			for (int i = floor + 1; i < NUM_FLOORS; ++i) {
				if (callsPending(i)) {
					return true;
				}
			}
		}
		else {
			for (int i = floor - 1; i > - 1; i -= 1) {
				if (callsPending(i)) {
					return true;
				}
			}
		}
		return false;
		
	}
	
	/**
	 * Returns an integer value of the number of up calls above the current floor if the 
	 * direction is up, and the number of down calls below the current floor if the direction is down.
	 *
	 * @param floor the floor
	 * @param dir the dir
	 * @return the int
	 */
	public int numUpAboveOrDownBelow(int floor, int dir) {
		int ans = 0;
		if (dir > 0) {
			for (int i = floor + 1; i < NUM_FLOORS; ++i) {
				ans += numCallsPending(i, dir);
			}
			return ans;
		}
		else {
			for (int i = floor - 1; i > -1; i--) {
				ans += numCallsPending(i, dir);
			}
			return ans;
		}
	}

}
