package elevatorSimulation.controllerObjects;

import elevatorSimulation.utility.InvalidParameterException;

/**
 * Interface for pending request processor impls.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.utility.InvalidParameterException
 */
public interface PendingReqsProcessor {
    
    /**
     * Method that the controller calls to add a request to the pending requests list.
     * 
     * @param flrNum integer representing the floor number that the request is coming from
     * @param dir integer representing the direction that the request is for, 1 for up, -1 for down
     */
    public void addPendReq(int flrNum, int dir);
    
    
    /**
     * Method that the controller calls when it is notified of an idle elevator to begin a pickup.
     * 
     * @param eNum elevator number of the elevator that is idle
     * 
     * @throws InvalidParameterException if invalid parameter exception is thrown up the call stack
     */
    public void beginPickup(int eNum) throws InvalidParameterException;
    
    
    /**
     * Method that the controller calls when an elevator starts movement to see if there are any pending requests that it can take.
     * 
     * @param eNum elevator number that is asking for the pending requests to be checked
     * 
     * @throws InvalidParameterException if invalid parameter exception is thrown up the call stack
     */
    public void checkPending(int eNum) throws InvalidParameterException;
}
