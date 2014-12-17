package elevatorSimulation.controllerObjects;

import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.utility.InvalidParameterException;
import java.util.ArrayList;

/**
 * Factory class to create an elevator selection impl.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see java.util.ArrayList
 */
public class ElevSelectionImplFactory {
    
    /**
     * Method to build an elevator selector object depending on the type of selector desired.
     * 
     * @param sType string representing the type of selector desired
     * @param elevators array list of elevators for which to pass new requests to
     * 
     * @return an elevator selector impl of desired type, of which the controller will use to delegate elevator selection to
     * 
     * @throws InvalidParameterException if string sType is not a supported selector
     */
    public static ElevatorSelector buildElevatorSelector(String sType, ArrayList<Elevator> elevators) throws InvalidParameterException{
        if (sType.equals("Standard")){
            return new StandardSelector(elevators);
        }
        else{
            throw new InvalidParameterException("There is currently no elevator selector of type " + sType + ".");
        }
    }
    
    
    // Private constructor to ensure no instances of ElevSelectionImplFactory are created
    private ElevSelectionImplFactory(){}
}
