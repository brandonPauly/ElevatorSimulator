package elevatorSimulation.elevatorObjects;

import elevatorSimulation.buildingObjects.Floor;
import elevatorSimulation.buildingObjects.Person;
import elevatorSimulation.utility.InvalidParameterException;

/**
 * This is an interface to describe the roll that elevator objects must fulfill.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.utility.InvalidParameterException
 */
public interface Elevator extends Runnable{
    
    /**
     * Gets the elevator number.
     * 
     * @return the number of the elevator
     */
    public int getElevNum();
    
    /**
     * Gets the floor number that an elevator is on.
     * 
     * @return the current floor of the elevator
     */
    public int getCurrentFloor();
    
    /**
     * Gets the direction that an elevator is traveling in.  Returns '0' for idle, '-1' for down, and '1' for up.
     * 
     * @return the direction of the elevator
     */
    public int getDirection();
    
    /**
     * Pushes a button on the elevator button panel.
     * 
     * @param floorNum the number of the button to push
     * 
     * @throws InvalidParameterException if floorNum parameter is not a button on the panel
     */
    public void pressFlrBtn(int floorNum) throws InvalidParameterException;
    
    /**
     * Updates the requests from the controller when a new floor request is given.
     * 
     * @param floorNum the floor number of the request
     * 
     * @throws InvalidParameterException if floorNum is for a non-existent floor
     */
    public void addFlrReq(int floorNum) throws InvalidParameterException;
    
    /**
     * Gets the rider count for an elevator.
     * 
     * @return the rider count for an elevator
     */
    public int getRiderCount();
    
    /**
     * Shuts down the elevator thread.
     */
    public void stop();
    
    /**
     * Loads a person onto the elevator.
     * 
     * @param p person to load
     */
    public void load(Person p);

    /**
     * Accessor for an elevator's maximum capacity.
     * 
     * @return the number of people the elevator can carry 
     */
    public int getMaxCapacity();
    
    /**
     * Method to unload any riders that are to get off on the floor the elevator is stopped on.
     * 
     * @param f floor to unload riders onto
     * 
     * @throws InvalidParameterException if invalid parameter exception is thrown up the call stack
     */
    public void unloadRiders(Floor f) throws InvalidParameterException;
    
    /**
     * Method to set an elevator to pickup mode.
     * 
     * @param floorNum the floor number for the pickup
     * @param dir integer representing the direction of the pickup request, 1 for up, -1 for down
     */
    public void setPickup(int floorNum, int dir);
    
    /**
     * Method to check if an elevator is on a pickup run.
     * 
     * @return true if the elevator is on a pickup run, false otherwise 
     */
    public boolean onPickup();
    
    /**
     * Accessor to get the pickup direction.
     * 
     * @return 1 if the pickup is going up, -1 if the pickup is down, 0 if there is no pickup
     */
    public int getPickupDir();

    /**
     * Accessor for the current pickup floor.
     * 
     * @return the pickup floor, -1 if not on a pickup
     */
    public int getPickupFloor();
}
