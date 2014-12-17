package elevatorSimulation.controllerObjects;

import elevatorSimulation.buildingObjects.Building;
import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.elevatorObjects.ElevatorFactory;
import elevatorSimulation.utility.InvalidParameterException;
import java.util.ArrayList;

/**
 * This class represents an elevator controller to manage floor requests and send those requests to particular elevator objects.
 * This is a singleton class.  The elevators are created from the controller and owned by the controller.
 * 
 * @author Brandon Pauly
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see elevatorSimulation.elevatorObjects.ElevatorFactory
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see java.util.ArrayList
 */
public final class Controller {
    
    /**
     * An array list to store the elevator objects for the building.
     */
    private static ArrayList<Elevator> elevators;
    
    /**
     * The controller instance, per the singleton design pattern.
     */
    private volatile static Controller controllerInstance;
    
    /**
     * The elevator selector impl to handle incoming floor requests.
     */
    private static ElevatorSelector reqsSelector;
    
    /**
     * The pending requests processor impl to manage pending requests.
     */
    private static PendingReqsProcessor reqsProcessor;
    
    /**
     * No current elevator available for pickup.
     */
   private final int NO_ELEVATOR = -1;
   
   /**
    * Counter for elevators that have finished their run.
    */
   private static int finishedElevators;
    
    /**
     * Method to get a reference to the single controller instance.
     * 
     * @param selector string representing the type of selector to use
     * @param processor string representing the type of processor to use
     * @param elevatorType string representing the type of elevator to construct
     * @param elevatorQuantity integer for the number of elevators to construct
     * @param doorTime integer for the number of milliseconds that the door is open on each floor
     * @param elevatorSpeed integer for the number of milliseconds it takes for the elevator to go from one floor to another
     * @param floorButtons integer for the number of floor buttons for the button panel in the elevators
     * @param defaultFloor integer for the default floor that the elevators go to
     * @param timeout integer for the number of milliseconds until the elevator times out and goes to the default floor
     * @param maxCapacity integer for the number of people that can be in an elevator at any given time
     * 
     * @return a reference to the controller instance
     * 
     * @throws InvalidParameterException if any parameters disallow object creation
     */
    public static Controller getControllerInstance(String selector, String processor, String elevatorType, int elevatorQuantity, int doorTime, 
            int elevatorSpeed, int floorButtons, int defaultFloor, int timeout, int maxCapacity) throws InvalidParameterException{
        if (getControllerInstance() == null){
            synchronized(Controller.class){
                if (getControllerInstance() == null){
                    setControllerInstance(new Controller(selector, processor, elevatorType, elevatorQuantity, doorTime, elevatorSpeed, floorButtons, 
                            defaultFloor, timeout, maxCapacity));
                }
            }
        }
        return getControllerInstance();
    }
    
    
    /**
     * Method that tells the controller that an elevator is idle.
     * 
     * @param eNum integer representing the identifying number of the elevator that is idle
     * 
     * @throws InvalidParameterException if PendingReqsProcessor.beginPickup() threw the exception
     */
    public static void elevIdle(int eNum) throws InvalidParameterException{
        getReqsProcessor().beginPickup(eNum);
    }
    
    
    /**
     * Method to scan the pending requests whenever an elevator begins movement.
     * 
     * @param elevNum integer representing the elevator number that is checking pending requests
     * 
     * @throws InvalidParameterException if PendingReqsProcessor.checkPending() threw the exception
     */
    public static void scanReqs(int elevNum) throws InvalidParameterException{
        getReqsProcessor().checkPending(elevNum);
    }
    
    
    /**
     * Method to give a floor request if the elevator selector has found an appropriate elevator for the floor request, otherwise
     *  the floor request is added to pending requests.
     * 
     * @param floorNum integer representing the floor number that the request came from
     * @param direction integer representing the direction for the request, 1 for up, -1 for down
     * 
     * @throws InvalidParameterException if Elevator.giveFloorRequest() threw the exception
     */
    public void addFloorRequest(int floorNum, int direction) throws InvalidParameterException{
        int elevator = getElevatorSelector().selectElevator(floorNum, direction);
        if (elevator == NO_ELEVATOR){
            getReqsProcessor().addPendReq(floorNum, direction);
        }
        else{
            giveFloorRequest(elevator, floorNum);
        }
    }
    
    
    /**
     * Sends a message to all elevators that people production has ended, and to finish up any trips that need to be made, then shut down.
     */
    public void stopElevators(){
        for (Elevator e : getElevators()){
            e.stop();
        }
    }
    
    
    public static void elevatorFinished(){
        int fin = incFinishedElevators();
        if (fin == getNumElevs()){
            Building.simulationFinished();
        }
    }
    
    
    /**
     * Gives a floor request to an elevator.
     * 
     * @param elevatorNumber number of elevator to give the request to
     * @param floorNumber number of floor to send elevator to
     * @throws InvalidParameterException if floor request is for a non-existent floor in the building
     */
    private void giveFloorRequest(int elevatorNumber, int floorNumber) throws InvalidParameterException{
        getElevators().get(elevatorNumber-1).addFlrReq(floorNumber);
    }
    
    
    /**
     * Private constructor for the controller class.
     * 
     * @param elevatorType string representing the type of elevator to construct
     * @param elevatorQuantity integer for the number of elevators to construct
     * @param doorTime integer for the number of milliseconds that the door is open on each floor
     * @param elevatorSpeed integer for the number of milliseconds it takes for the elevator to go from one floor to another
     * @param floorButtons integer for the number of floor buttons for the button panel in the elevators
     * @param defaultFloor integer for the default floor that the elevators go to
     * @param timeout integer for the number of milliseconds until the elevator times out and goes to the default floor
     * @param maxCap integer for the number of people that can be in an elevator at any given time
     * @throws InvalidParameterException if any parameters disallow object creation
     */
    private Controller(String selector, String processor, String elevatorType, int elevatorQuantity, int doorTime, int elevatorSpeed, 
            int floorButtons, int defaultFloor, int timeout, int maxCap) throws InvalidParameterException{
        if (maxCap < 0){
            throw new InvalidParameterException("Maximum elevator capacity must be nonnegative.");
        }
        setElevators(new ArrayList());
        createElevators(elevatorType, elevatorQuantity, doorTime, elevatorSpeed, floorButtons, defaultFloor, timeout, maxCap);
        setReqsSelector(ElevSelectionImplFactory.buildElevatorSelector(selector, getElevators()));
        setReqsProcessor(ProcPendReqsFactory.buildPenReqsProcessor(processor, getElevators()));
        setFinishedElevators(0);
    }
    
    
    /**
     * Creates the elevators and puts them into the elevator array.
     * 
     * @param elevatorType string to represent the elevator type to be constructed
     * @param elevatorQuantity number of elevators to install
     * @param doorTime number of milliseconds that the doors are open for
     * @param elevatorSpeed number of milliseconds that it takes an elevator to travel from one floor to another
     * @param floorButtons number of buttons for the button panel
     * @param defaultFloor number of the floor for the default floor
     * @param timeout number of milliseconds before the elevator returns to the default floor
     * @throws InvalidParameterException if elevator creation cannot take place
     */
    private void createElevators(String elevatorType, int elevatorQuantity, int doorTime, int elevatorSpeed, 
            int floorButtons, int defaultFloor, int timeout, int maxCapacity) throws InvalidParameterException{
        for (int i = 0; i < elevatorQuantity; i++){
            Elevator e = ElevatorFactory.buildElevator(elevatorType, i + 1, doorTime, elevatorSpeed, floorButtons, defaultFloor, timeout, maxCapacity);
            Thread t = new Thread(e);
            t.start();
            getElevators().add(e);
        }
    }
    
    
    /**
     * Mutator to set the quantity of elevators that have finished their run.
     * 
     * @param quantity the number of elevators that have finished
     */
    private void setFinishedElevators(int quantity){
        finishedElevators = quantity;
    }
    
    
    /**
     * Mutator/Accessor to increment the number of elevators that have completed their run and return that number.
     * 
     * @return the number of finished elevators after incrementing
     */
    private static int incFinishedElevators(){
        return ++finishedElevators;
    }
    
    
    /**
     * Accessor to get the number of elevators under this controller's control.
     * 
     * @return the number of elevators
     */
    private static int getNumElevs(){
        return getElevators().size();
    }
    
    /**
     * Accessor to get the array list of elevators that this controller controls.
     * 
     * @return the array list of elevators 
     */
    private static ArrayList<Elevator> getElevators(){
        return elevators;
    }
    
    
    /**
     * Accessor for the instance of controller.
     * 
     * @return the controller instance, null if there isn't any current instance
     */
    private static Controller getControllerInstance(){
        return controllerInstance;
    }
    
    
    /**
     * Mutator for the controller instance.
     * 
     * @param cont the controller to set the instance to
     */
    private static void setControllerInstance(Controller cont){
        controllerInstance = cont;
    }
    
    
    /**
     * Accessor for the request processor.
     * 
     * @return the request processor that manages pending requests
     */
    private static PendingReqsProcessor getReqsProcessor(){
        return reqsProcessor;
    }
    
    
    /**
     * Accessor for the elevator selector.
     * 
     * @return the elevator selector that checks for an available elevator
     */
    private static ElevatorSelector getElevatorSelector(){
        return reqsSelector;
    }
    
    
    /**
     * Mutator for the elevator list.
     * 
     * @param elevs the list of elevators to set to
     */
    private void setElevators(ArrayList<Elevator> elevs){
        elevators = elevs;
    }
    
    
    /**
     * Mutator for the request selector.
     * 
     * @param selector the selector to set for elevator selection
     */
    private void setReqsSelector(ElevatorSelector selector){
        reqsSelector = selector;
    }
    
    
    /**
     * Mutator for the request processor.
     * 
     * @param proc the processor to set for managing pending requests 
     */
    private void setReqsProcessor(PendingReqsProcessor proc){
        reqsProcessor = proc;
    }
}
