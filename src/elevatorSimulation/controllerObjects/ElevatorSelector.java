package elevatorSimulation.controllerObjects;

/**
 * Interface for elevator selector impls.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.controllerObjects.StandardProcessor
 */
public interface ElevatorSelector {
    
    /**
     * When a new floor request is made, the method selects an elevator if one is available.
     * 
     * @param floorNum integer representing the floor number that the new request is from
     * @param direction integer representing the direction that the new request is for, 1 for up, -1 for down
     * 
     * @return returns the number of the elevator to give the request to, returns -1 if there is no elevator available to take the request
     */
    public int selectElevator(int floorNum, int direction);
}
