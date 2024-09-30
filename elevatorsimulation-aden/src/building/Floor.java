package building;
// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import genericqueue.GenericQueue;

/*
 * Class belongs to: Henry
 * Reviewed by: Joseph
 * Additional Comments:
 */


// TODO: Auto-generated Javadoc
/**
 * The Class Floor. This class provides the up/down queues to hold
 * Passengers as they wait for the Elevator.
 */
public class Floor {

	/** The queues to represent Passengers going UP or DOWN */	
	private GenericQueue<Passengers> down;
	private GenericQueue<Passengers> up;

	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	
	// TODO: Write the helper methods needed for this class. 
	// You probably will only be accessing one queue at any
	// given time based upon direction - you could choose to 
	// account for this in your methods.
	
	
	/**
	 * Returns whether the up or down queue is empty corresponding to the direction
	 *
	 * @param dir the direction of the elevator.
	 * @return true, if elevator is empty
	 */
	public boolean empty(int dir) {
		if(dir > 0) {
			return (up.isEmpty());
		} else {
			return (down.isEmpty());
		}
		
	}
	
	/**
	 * Returns a boolean depending on if the add was successful or not
	 *
	 * @param object the passenger object
	 * @param dir the direction of the elevator.
	 * @return true, if the add was successful
	 */
	public boolean add(Passengers object, int dir) {
		if(dir > 0) {
			return (up.add(object));
		} else {
			return (down.add(object));
		}
	}
	
	/**
	 * Returns a passenger object for the correct queue, from the top of the stack
	 *
	 * @param dir the direction of the elevator.
	 * @return the passenger object from the correct queue
	 */
	public Passengers peek(int dir) {
		if(dir > 0) {
			return (up.peek());
		} else {
			return (down.peek());
		}
				
	}
	/**
	 * 
	 * @param dir
	 * @return Returns the size of a queue of a certain direction
	 */
	public int size (int dir) {
		if (dir > 0) {
			return up.size();
		}
		return down.size();
	}

	/**
	 * Returns a passenger object. Returns null if empty.
	 *
	 * @param dir the direction of the elevator.
	 * @return the passenger object from the correct queue
	 */
	public Passengers poll(int dir) {
		if(dir > 0) {
			return (up.poll());
		} else {
			return (down.poll());
		}
	}
	
	public GenericQueue<Passengers> getDown() {
		return down;
	}

	public GenericQueue<Passengers> getUp() {
		return up;
	}

	/**
	 * Queue string. This method provides visibility into the queue
	 * contents as a string. What exactly you would want to visualize 
	 * is up to you
	 *
	 * @param dir determines which queue to look at
	 * @return the string of queue contents
	 */
	String queueString(int dir) {
		String str = (dir == Elevator.UP) ? "UP: " : "DOWN: ";
		ListIterator<Passengers> list;
		list = (dir == Elevator.UP) ?up.getListIterator() : down.getListIterator();
		if (list != null) {
			while (list.hasNext()) {
				// choose what you to add to the str here.
				str += list.next().getNumPass();
				if (list.hasNext()) str += ",";
			}
		}
		return str;	
	}
	
	
}
