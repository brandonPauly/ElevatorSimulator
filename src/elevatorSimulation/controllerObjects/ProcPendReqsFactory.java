package elevatorSimulation.controllerObjects;

import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.utility.InvalidParameterException;
import java.util.ArrayList;

/**
 * Factory class to create a pending requests processor impl for the elevator controller.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see java.util.ArrayList
 */
public class ProcPendReqsFactory {
    
    /**
     * Method to build a pending request processor for the elevator controller.
     * 
     * @param processorType string representing the type of processor to build for the elevator controller
     * @param elevators array list of elevators for the processor to give pending requests to
     * 
     * @return the desired pending requests processor impl for the controller to delegate to
     * 
     * @throws InvalidParameterException if the input string doesn't match a supported processor type
     */
    public static PendingReqsProcessor buildPenReqsProcessor(String processorType, ArrayList<Elevator> elevators) throws InvalidParameterException{
        if (processorType.equals("Standard")){
            return new StandardProcessor(elevators);
        }
        else{
            throw new InvalidParameterException("There is currently no pending requests processor of type " + processorType + ".");
        }
    }
    
    
    // private constructor to ensure no ProcPendReqsFactory are instantiated
    private ProcPendReqsFactory(){}
}
