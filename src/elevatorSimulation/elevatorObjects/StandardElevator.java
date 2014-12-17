package elevatorSimulation.elevatorObjects;

import elevatorSimulation.buildingObjects.Floor;
import elevatorSimulation.buildingObjects.Person;
import elevatorSimulation.utility.InvalidParameterException;


/**
 * This class is an elevator abstraction, that hides its implementation by using a delegate.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see elevatorSimulation.buildingObjects.Floor
 * @see elevatorSimulation.buildingObjects.Person
 */
public class StandardElevator implements Elevator, Runnable{
    
    /**
     * Reference to the delegate for the elevator.
     */
    private final Elevator elevatorImpl;
    
    
    /**
     * Constructor to create a standard elevator.
     * 
     * @param elevatorNumber number that identifies the elevator
     * @param doorTime number of milliseconds that the doors are open at a stop
     * @param elevatorSpeed the number of milliseconds that the elevator takes to travel from one floor to another
     * @param floorButtons number of floor buttons for the button panel
     * @param defaultFloor number for the default floor of the elevator
     * @param timeout number of milliseconds before the elevator times out
     * @param maxCap the maximum capacity of people that an elevator can hold at any given time
     */
    public StandardElevator(int elevatorNumber, int doorTime, int elevatorSpeed, int floorButtons, int defaultFloor, int timeout, int maxCap){
        elevatorImpl = ElevatorImplFactory.buildElevatorImpl(elevatorNumber, doorTime, elevatorSpeed, floorButtons, defaultFloor, timeout, maxCap);
    }

    
    /**
     * Method to get the elevator number that identifies the elevator.
     * 
     * @return the elevator number
     */
    @Override
    public int getElevNum() {
        return getElevatorImpl().getElevNum();
    }

    
    /**
     * Method to get the current floor of the elevator.
     * 
     * @return the current floor that the elevator is on
     */
    @Override
    public int getCurrentFloor() {
        return getElevatorImpl().getCurrentFloor();
    }

    
    /**
     * Method to get the direction the elevator is traveling.
     * 
     * @return '0' for idle, '-1' for down, '1' for up
     */
    @Override
    public int getDirection() {
        return getElevatorImpl().getDirection();
    }

    
    /**
     * Method to press a floor button on the button panel.
     * 
     * @param floorNum the number of the button to press
     * 
     * @throws InvalidParameterException if floorNum is not a button on the panel
     */
    @Override
    public void pressFlrBtn(int floorNum) throws InvalidParameterException {
        getElevatorImpl().pressFlrBtn(floorNum);
    }

    
    /**
     * Method to add a floor request.
     * 
     * @param floorNum the number of the floor to stop at
     * 
     * @throws InvalidParameterException if the floor is not a floor that exists in the building
     */
    @Override
    public void addFlrReq(int floorNum) throws InvalidParameterException {
        getElevatorImpl().addFlrReq(floorNum);
    }

    
    /**
     * Method to run the elevator thread.
     */
    @Override
    public void run() {
        getElevatorImpl().run();
    }
    
    
    /**
     * Method to get the rider count on an elevator.
     * 
     * @return the number of riders presently on the elevator
     */
    @Override
    public int getRiderCount(){
        return getElevatorImpl().getRiderCount();
    }
    
    
    /**
     * Method to shut down the elevator.
     */
    @Override
    public void stop(){
        getElevatorImpl().stop();
    }
    
    
    /**
     * Method to load a person onto the elevator.
     * 
     * @param p person to load
     */
    @Override
    public void load(Person p){
        getElevatorImpl().load(p);
    }
    
    
    /**
     * Accessor for the elevators maximum capacity.
     * 
     * @return the number of people the elevator can successfully hold at one time
     */
    @Override
    public int getMaxCapacity(){
        return getElevatorImpl().getMaxCapacity();
    }
    
    
    /**
     * Method to unload riders when an elevator stops at a floor.
     * 
     * @param f floor to unload riders onto
     * 
     * @throws InvalidParameterException if Person.getOffElevator() throws the exception
     */
    @Override
    public void unloadRiders(Floor f) throws InvalidParameterException{
        getElevatorImpl().unloadRiders(f);
    }
    
    
    /**
     * Mutator to set the elevator on a pickup run.
     * 
     * @param flrNum floor number for pickup
     * @param dir the direction of the pickup, 1 for up, -1 for down
     */
    @Override
    public void setPickup(int flrNum, int dir){
        getElevatorImpl().setPickup(flrNum, dir);
    }
    
    
    /**
     * Accessor to check if an elevator is on a pickup.
     * 
     * @return true if the elevator is on a pickup run, false otherwise 
     */
    @Override
    public boolean onPickup(){
        return getElevatorImpl().onPickup();
    }
    
    
    /**
     * Accessor for the pickup direction.
     * 
     * @return 1 if the pickup is going up, -1 if the pickup is down, and 0 if there is no pickup
     */
    @Override
    public int getPickupDir(){
        return getElevatorImpl().getPickupDir();
    }
    
    
    /**
     * Accessor for the current pickup floor.
     * 
     * @return the pickup floor, -1 if not on a pickup
     */
    @Override
    public int getPickupFloor(){
        return getElevatorImpl().getPickupFloor();
    }
    
    
    /**
     * Accessor for the elevator impl.
     * 
     * @return the elevator impl that the standard elevator delegates to
     */
    private Elevator getElevatorImpl(){
        return elevatorImpl;
    }
}
