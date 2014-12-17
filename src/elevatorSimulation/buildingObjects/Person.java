package elevatorSimulation.buildingObjects;

import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.utility.InvalidParameterException;

/**
 * This class represents a person in the context of the elevator simulator. Each person starts on a floor, 
 * and has a destination floor.  When a proper elevator arrives, the person enters the elevator and pushes their
 *  desired floor button.  Then rides the elevator until the elevator stops on their floor.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see elevatorSimulation.utility.LogMgr
 */
public class Person {
    
    /**
     * the floor the person is on, if they are not on an elevator
     */
    private Floor currentFloor;
    
    /**
     * the current elevator the person is on, if they are not on a floor
     */
    private Elevator currentElevator;
    
    /**
     * integer representing the floor number that the person desires to travel to
     */
    private final int destination;
    
    /**
     * integer representing the floor number that the person came from
     */
    private final int sourceFloor;
    
    /**
     * integer of which to identify the person by
     */
    private final int personNumber;
    
    /**
     * boolean for whether or not the person is waiting for an elevator
     */
    private boolean isWaiting;
    
    /**
     * the beginning of the person's wait time for an elevator
     */
    private long waitStart;
    
    /**
     * the end of the person's wait time for an elevator
     */
    private long waitEnd;
    
    /**
     * the beginning of the person's ride time to their destination
     */
    private long rideStart;
    
    /**
     * the end of the person's ride time to their destination
     */
    private long rideEnd;
    
    /**
     * the total wait time in milliseconds
     */
    private long waitTime;
    
    /**
     * the total ride time in milliseconds
     */
    private long rideTime;
    
    
    
    /**
     * Constructor to create a person, and place them on their starting floor.
     * 
     * @param identifier integer to identify the person by
     * @param srcFlr floor to place the person on
     * @param dest integer representing the destination of the person
     */
    public Person(int identifier, Floor srcFlr, int dest) {
        personNumber = identifier;
        sourceFloor = srcFlr.getFlrNum();
        setCurrentFloor(srcFlr);
        setCurrentElevator(null);
        destination = dest;
        setIsWaiting(true);
        setWaitStart(System.currentTimeMillis());
    }
    
    
    /**
     * Method to board the elevator if the elevator is going in the right direction and is not already full.
     * 
     * @param elevator the elevator to get on
     * 
     * @throws InvalidParameterException if Elevator.load() is passed a null person object
     */
    public void getOnElevator(Elevator elevator) throws InvalidParameterException{
        setCurrentFloor(null);
        setIsWaiting(false);
        setWaitEnd(System.currentTimeMillis());
        calculateWaitTime();
        setCurrentElevator(elevator);
        getCurrentElevator().load(this);
        setRideStart(System.currentTimeMillis());
        getCurrentElevator().pressFlrBtn(getDestination());
    }
    
    
    /**
     * Method to get off the elevator if the elevator has stopped on the desired floor.
     * 
     * @param floor the floor to be unloaded onto
     * 
     * @throws InvalidParameterException if Floor.load() is passed a null person object
     */
    public void getOffElevator(Floor floor) throws InvalidParameterException{
        setCurrentFloor(floor);
        getCurrentFloor().load(this);
        setRideEnd(System.currentTimeMillis());
        calculateRideTime();
        setCurrentElevator(null);
    }
    
    /**
     * Is the person waiting for an elevator?
     * @return true if the person is waiting for an elevator
     */
    public boolean isWaiting(){
        return isWaiting;
    }
    
    
    /**
     * Method to get an individuals source floor.
     * 
     * @return the floor number the individual was created on 
     */
    public int getSrcFlr(){
        return sourceFloor;
    }
    
    
    /**
     * Method to get the floor number that the person desires to travel to.
     * 
     * @return the floor number the individual desires to travel to 
     */
    public int getDestination(){
        return destination;
    }
    
    
    /**
     * Method to press the up button on a floor, when a person wants to travel up.
     * 
     * @throws InvalidParameterException if Floor.pressUp() has thrown the exception from the call stack
     */
    public void pressUp() throws InvalidParameterException{
        getCurrentFloor().pressUp(getPersonNumber());
    }
    
    
    /**
     * Method to press the down button on a floor, when a person wants to travel down.
     * 
     * @throws InvalidParameterException if Floor.pressDown() has thrown the exception from the call stack
     */
    public void pressDown() throws InvalidParameterException{
        getCurrentFloor().pressDown(getPersonNumber());
    }
    
    
    /**
     * Method to get the number that identifies the person.
     * 
     * @return the integer identifying the person 
     */
    public int getPersonNumber(){
        return personNumber;
    }
    
    
    /**
     * Accessor for the total time the person waited for an elevator.
     * 
     * @return the time the person waited for an elevator in milliseconds
     */
    public long getWaitTime(){
        return waitTime;
    }
    
    
    /**
     * Accessor for the total time the person rode the elevator for.
     * 
     * @return the time the person waited for an elevator in milliseconds
     */
    public long getRideTime(){
        return rideTime;
    }
    
    
    /**
     * Mutator to change the person's floor they are on.
     * 
     * @param floor the floor that the person is on
     */
    private void setCurrentFloor(Floor floor){
        if (floor == null){
            currentFloor = null;
        }
        else{
            currentFloor = floor;
        }
    }
    
    
    /**
     * Mutator to change the person's elevator.
     */
    private void setCurrentElevator(Elevator elevator){
        if (elevator == null){
            currentElevator = null;
        }
        else{
            currentElevator = elevator;
        }
    }
    
    /**
     * Mutator for the isWaiting boolean.
     * 
     * @param setTo true if the person is waiting for an elevator
     */
    private void setIsWaiting(boolean setTo){
        isWaiting = setTo;
    }
    
    
    /**
     * Mutator for the start of the wait timer.
     * 
     * @param startTime the system time in milliseconds that the wait starts 
     */
    private void setWaitStart(long startTime){
        waitStart = startTime;
    }
    
    
    /**
     * Mutator for the end of the wait timer.
     * 
     * @param waitTime the system time in milliseconds that the wait ends 
     */
    private void setWaitEnd(long endTime){
        waitEnd = endTime;
    }
    
    
    /**
     * Mutator for the start of the ride timer.
     * 
     * @param startTime the system time in milliseconds that the ride starts 
     */
    private void setRideStart(long startTime){
        rideStart = startTime;
    }
    
    
    /**
     * Mutator for the end of the ride timer.
     * 
     * @param endTime the system time in milliseconds that the ride ends 
     */
    private void setRideEnd(long startTime){
        rideEnd = startTime;
    }
    
    
    /**
     * Calculates and updates wait time.
     */
    private void calculateWaitTime(){
        setWaitTime(getWaitEnd() - getWaitStart());
    }
    
    
    /**
     * Calculates and updates ride time.
     */
    private void calculateRideTime(){
        setRideTime(getRideEnd() - getRideStart());
    }
    
    
    /**
     * Mutator to set the time the person waited for an elevator.
     * 
     * @param wT the time the person waited in milliseconds 
     */
    private void setWaitTime(long wT){
        waitTime = wT;
    }
    
    
    /**
     * Mutator to set the time the person rode the elevator for.
     * 
     * @param wT the time the person waited in milliseconds 
     */
    private void setRideTime(long rT){
        rideTime = rT;
    }
    
    
    /**
     * Accessor for the wait end.
     * 
     * @return the time the wait ended
     */
    private long getWaitEnd(){
        return waitEnd;
    }
    
    
    /**
     * Accessor for the wait start.
     * 
     * @return the time the wait started
     */
    private long getWaitStart(){
        return waitStart;
    }
    
    
    /**
     * Accessor for the ride end.
     * 
     * @return the time the ride ended
     */
    private long getRideEnd(){
        return rideEnd;
    }
    
    
    /**
     * Accessor for the ride start.
     * 
     * @return the time the ride started
     */
    private long getRideStart(){
        return rideStart;
    }
    
    
    /**
     * Accessor for the current elevator.
     * 
     * @return the current elevator the person is on, null if person isn't a rider 
     */
    private Elevator getCurrentElevator(){
        return currentElevator;
    }
    
    
    /**
     * Accessor for the current floor.
     * 
     * @return the current floor the person is on, null if the person is riding an elevator
     */
    private Floor getCurrentFloor(){
        return currentFloor;
    }
}
