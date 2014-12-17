package elevatorSimulation.utility;

/**
 * A simple exception class to throw a general exception if an invalid parameter is provided.
 * 
 * @author Brandon Pauly
 */
public class InvalidParameterException extends Exception{
    
    public InvalidParameterException(String msg){
        super(msg);
    }
}
