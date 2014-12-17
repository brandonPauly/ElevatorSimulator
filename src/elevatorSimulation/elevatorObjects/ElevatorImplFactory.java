package elevatorSimulation.elevatorObjects;


/**
 * This class is a factory to create a delegate for an elevator.
 * 
 * @author Brandon Pauly
 */
public class ElevatorImplFactory {
    
    /**
     * Static method build an elevator impl for an elevator object to delegate to.
     * 
     * @param elevatorNumber the number to identify the elevator with
     * @param doorTime the number of milliseconds that the doors are open for at a stop
     * @param elevatorSpeed the number of milliseconds that the elevator takes to go from one floor to another
     * @param floorButtons the number of buttons for the button panel
     * @param defaultFloor the default floor for the elevator
     * @param timeout the number of milliseconds before the elevator times out
     * @param maxCap the maximum number of people that can be on an elevator at a given time
     * @return the appropriate elevator impl to delegate to
     */
    public static Elevator buildElevatorImpl(int elevatorNumber, int doorTime, int elevatorSpeed, int floorButtons, int defaultFloor, 
            int timeout, int maxCap){
        return new ElevatorImpl(elevatorNumber, doorTime, elevatorSpeed, floorButtons, defaultFloor, timeout, maxCap);
    }
    
    /**
     * Private constructor to the ElevatorImplFactory is never instantiated.
     */
    private ElevatorImplFactory(){}
}
