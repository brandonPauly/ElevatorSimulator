package elevatorSimulation.elevatorObjects;

import elevatorSimulation.utility.InvalidParameterException;

/**
 * This class is a factory to create elevator objects for the controller.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.utility.InvalidParameterException
 */
public class ElevatorFactory {
    
    /**
     * Method to build an elevator object.
     * 
     * @param elevatorType the type of elevator to construct
     * @param elevatorNumber the elevator number
     * @param doorTime the number of milliseconds that the doors are open
     * @param elevatorSpeed the number of milliseconds that an elevator takes to travel from floor to floor
     * @param floorButtons the number of floor buttons to put in the button panel
     * @param defaultFloor the default floor that the elevator travels to after timeout
     * @param timeout the number of milliseconds before the elevator timeout
     * @param maxCap the maximum capacity of the elevator
     * @return an appropriate elevator object
     * @throws InvalidParameterException if a string other than "Standard" is passed as the elevatorType
     */
    public static Elevator buildElevator(String elevatorType, int elevatorNumber, int doorTime, int elevatorSpeed, int floorButtons, 
            int defaultFloor, int timeout, int maxCap) throws InvalidParameterException{
        if (elevatorType.equals("Standard")){  // Standard is currently the only elevator type implemented
            return new StandardElevator(elevatorNumber, doorTime, elevatorSpeed, floorButtons, defaultFloor, timeout, maxCap);
        }
        else{
            throw new InvalidParameterException("No elevator of type " + elevatorType + "is currently implemented.");
        }
    }
    
    /**
     * Private constructor to ensure no ElevatorFactory objects are constructed
     */
    private ElevatorFactory(){}
    
}
