package elevatorSimulation.elevatorObjects;

import elevatorSimulation.buildingObjects.Building;
import elevatorSimulation.buildingObjects.Floor;
import elevatorSimulation.buildingObjects.Person;
import elevatorSimulation.controllerObjects.Controller;
import elevatorSimulation.utility.InvalidParameterException;
import elevatorSimulation.utility.LogMgr;
import java.util.ArrayList;
import java.util.HashSet;




/**
 * This class represents the general delegate for the Elevator objects.  The most common behaviors and standard attributes for 
 * an elevator are implemented here.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.buildingObjects.Building
 * @see elevatorSimulation.buildingObjects.Person
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see elevatorSimulation.utility.LogMgr
 * @see java.util.ArrayList
 * @see elevatorSimulation.controllerObjects.Controller
 * @see elevatorSimulation.buildingObjects.Floor
 */
public class ElevatorImpl implements Elevator, Runnable {
    
    /**
     * The button panel in the elevator.
     */
    private final boolean[] buttonPanel;
    
    /**
     * The number of passengers in the elevator.
     */
    private int passengerCount;
    
    /**
     * The current floor the elevator is on.
     */
    private int currentFloor;
    
    /**
     * The number identifying the elevator.
     */
    private final int elevatorNumber;
    
    /**
     * The array that represents the floor requests for the elevator.
     */
    private final boolean[] floorRequests;
    
    /**
     * The array that stores the elevator riders.
     */
    private ArrayList<Person> riders;
    
    /**
     * Number of milliseconds the doors stay open on the elevator.
     */
    private final int doorTime;
    
    /**
     * Number of milliseconds the elevator takes to go from one floor to another.
     */
    private final int elevatorSpeed;
    
    /**
     * Number for the default floor.
     */
    private final int defaultFloor;
    
    /**
     * Number for the direction the elevator is traveling in.
     */
    private int direction;
    
    /**
     * The elevator is either on or off.
     */
    private boolean running;
    
    /**
     * A constant representing up: {@value}.
     */
    private static final int UP = 1;
    
    /**
     * A constant representing down: {@value}.
     */
    private static final int DOWN = -1;
    
    /**
     * A constant representing an idle elevator: {@value}.
     */
    private static final int IDLE = 0;
    
    /**
     * The number of milliseconds before the elevator times out.
     */
    private final int idleTime;
        
    /**
     * The maximum capacity of the elevator.
     */
    private final int maxCapacity;
    
    /**
     * Boolean representing if the elevator is on a pickup or not.
     */
    private boolean onPickup;
    
    /**
     * Direction of the pickup, if on one.
     */
    private int pickupDir;
    
    /**
     * Floor for pickup, -1 if no current pickup
     */
    private int pickupFloor;
    
    /**
     * Constant for a pickup floor that does not exist.
     */
    private final int NO_SUCH_FLOOR = -1;
    
    /**
     * Constructor for a new elevator impl for elevators to delegate to.
     * 
     * @param elevNum number to identify the elevator by
     * @param drTime number of milliseconds that the doors are open
     * @param elevSpeed number of milliseconds that it takes the elevator to travel from one floor to another
     * @param floorButtons number of floor buttons to put in the button panel
     * @param dfltFloor the default floor for the elevator
     * @param timeout the number of milliseconds before the elevator times out
     * @param maxRiders maximum number of riders that can be on an elevator at any given time
     */
    public ElevatorImpl(int elevNum, int drTime, int elevSpeed, int floorButtons, int dfltFloor, int timeout, int maxRiders){
        setRiders(new ArrayList());
        buttonPanel = new boolean[floorButtons];
        floorRequests = new boolean[floorButtons];
        elevatorNumber = elevNum;
        doorTime = drTime;
        elevatorSpeed = elevSpeed;   
        defaultFloor = dfltFloor;
        setDirection(IDLE);  // sets elevator to idle upon creation
        startRunning();  // keeps the elevator running
        idleTime = timeout;
        setCurrentFloor(dfltFloor);  // starts elevator on default floor
        setPassengerCount(0);
        maxCapacity = maxRiders;
        turnOffPickup();
        setPickupFloor(NO_SUCH_FLOOR);
    }
    
    
    /**
     * This method runs the elevator object once the thread is started.  The elevator goes to sleep if there are not any floors that it needs to visit.
     * If there is a request, the move method is called to get the elevator in motion.
     */
    @Override
    public void run(){
        long timeout = getIdleTime();
        long elapsed;
        long waitStart = 0, waitEnd = 0;
        while (getRunning() || stopsRemain() || getCurrentFloor() != getDefaultFloor()){
            if (!stopsRemain()){                 // if no stops remain, wait for a new request to be made
                setDirection(IDLE);
                synchronized(this){
                    try {
                        waitStart = System.currentTimeMillis();
                        wait(timeout);
                        waitEnd = System.currentTimeMillis();
                    } 
                    catch (InterruptedException e) {
                        System.out.println("Interrupted exception ended the wait on elevator " + getElevNum() + ".  " + e.getMessage());
                    }
                }
                elapsed = waitEnd - waitStart;

                if ((elapsed >= timeout) && (getCurrentFloor() != getDefaultFloor())){  // if the timeout time has elapsed since the last
                    try {                                                                  // request, and the elevator is not on the default
                        timeout = getIdleTime();
                        setPickup(1, UP);
                        addFlrReq(getDefaultFloor());                                           // floor, add a request to go to the default floor
                        LogMgr.defaultFloorLog(getElevNum(), getDefaultFloor());
                    } 
                    catch (InvalidParameterException e) {
                        System.out.println("Invalid default floor.  Default floor must be a floor that exists on the building.");}
                }
                if (!stopsRemain() && getCurrentFloor() != getDefaultFloor()){     // if there are no stops again, return to beginning of loop
                    timeout -= elapsed;
                    if (timeout < 0){    // to ensure wait is not called with a negative value
                        timeout = 0;
                    }
                    continue;
                }
            }
            if (stopsRemain()){       // call move method if there is a new request
                timeout = getIdleTime();
                try {
                    move();
                } catch (InvalidParameterException ex) {
                    System.out.println("Invalid floor request made.");
                }
            }
        }
        Controller.elevatorFinished();
    }
    
    
    /**
     * This method presses a floor button within the elevator.  If the request is not in the direction of the elevator's travel, the request is ignored.
     * 
     * @param floorNum the button number of the floor to travel to
     * 
     * @throws InvalidParameterException if the floor doesn't exist on the button panel
     */
    @Override
    public void pressFlrBtn(int floorNum) throws InvalidParameterException {
        if (floorNum < 1 || floorNum > getButtonPanel().length){
            throw new InvalidParameterException("Integer cannot be greater than the number of floors, nor can it be less than 1.");
        }
        if (getDirection() != IDLE){                                                 // ensures floor button pushed is in direction of travel
            synchronized(getFloorRequests()){
                if (getDirection() == UP){                                               // and updates button panel accordingly
                    if (floorNum > getCurrentFloor()){
                        getButtonPanel()[floorNum-1] = true;
                        LogMgr.riderReqAddedLog(getElevNum(), floorNum, getButtonPanel(), getFloorRequests());
                    }
                    else{
                        LogMgr.improperRiderReqLog(getElevNum(), floorNum);
                    }
                }
                else if (getDirection() == DOWN){
                    if (floorNum < getCurrentFloor()){
                        getButtonPanel()[floorNum-1] = true;
                        LogMgr.riderReqAddedLog(getElevNum(), floorNum, getButtonPanel(), getFloorRequests());
                    }
                    else{
                        LogMgr.improperRiderReqLog(getElevNum(), floorNum);
                    }
                }
            }
        }
        else{                                                          // if elevator is idle, request is added, and all elevators are woken up
            synchronized(this){
                if (floorNum > getCurrentFloor()){
                    setDirection(UP);
                }
                else{
                    setDirection(DOWN);
                }
                getButtonPanel()[floorNum-1] = true;
                notifyAll();
                LogMgr.riderReqAddedLog(getElevNum(), floorNum, getButtonPanel(), getFloorRequests());
            }
        }
    }
    
    
    /**
     * Method to add a floor request from the elevator controller.
     * 
     * @param floorNum the number of the floor to add a request for
     * 
     * @throws InvalidParameterException if the floor doesn't exist within the building
     */
    @Override
    public void addFlrReq(int floorNum) throws InvalidParameterException {
        
        if (floorNum < 1 || floorNum > getButtonPanel().length){
            throw new InvalidParameterException("Integer cannot be greater than the number of floors, nor can it be less than 1.");
        }
        if (getDirection() != IDLE){                                          // ensures floor request is appropriate for direction of travel
            synchronized(getFloorRequests()){
                if (getDirection() == UP){                                        // and updates floor request array accordingly
                    if (floorNum >= getCurrentFloor()){
                        getFloorRequests()[floorNum-1] = true;
                        LogMgr.floorReqAddedLog(getElevNum(), floorNum, getButtonPanel(), getFloorRequests());
                    }
                }
                else if (getDirection() == DOWN){                          
                    if (floorNum <= getCurrentFloor()){
                        getFloorRequests()[floorNum-1] = true;
                        LogMgr.floorReqAddedLog(getElevNum(), floorNum, getButtonPanel(), getFloorRequests());
                    }
                }
            }
        }
        else{                                                           // if elevator is idle, floor request is added and all elevators
            synchronized(this){                                         // are woken up
                if (getCurrentFloor() == floorNum){
                    doorAction();
                    if (onPickup() && getPickupFloor() == getCurrentFloor()){
                        setPickupFloor(-1);
                        turnOffPickup();
                    }
                    notifyAll();
                    return;
                }
                if (floorNum > getCurrentFloor()){
                    setDirection(UP);
                }
                else{
                    setDirection(DOWN);
                }
                getFloorRequests()[floorNum-1] = true;
                notifyAll();
                LogMgr.floorReqAddedLog(getElevNum(), floorNum, getButtonPanel(), getFloorRequests());
            }
        }
    }
    
    
    /**
     * Mutator to set the elevator on a pickup run.
     * 
     * @param dir the direction of the pickup, 1 for up, -1 for down
     */
    @Override
    public void setPickup(int floorNum, int dir){
        pickupFloor = floorNum;
        onPickup = true;
        pickupDir = dir;
    }
    
    
    /**
     * Accessor to check if an elevator is on a pickup.
     * 
     * @return true if the elevator is on a pickup run, false otherwise 
     */
    @Override
    public boolean onPickup(){
        return onPickup;
    }
    
    
    /**
     * Accessor for the elevator number.
     * 
     * @return the elevator number to identify the elevator by
     */
    @Override
    public int getElevNum(){
        return elevatorNumber;
    }
    
    
    /**
     * Accessor for the pickup direction.
     * 
     * @return 1 if the pickup is going up, -1 if the pickup is down, and 0 if there is no pickup
     */
    @Override
    public int getPickupDir(){
        return pickupDir;
    }
    
    
    /**
     * Get the current floor.
     * 
     * @return the current floor the elevator is on
     */
    @Override
    public int getCurrentFloor(){
        return currentFloor;
    }
    
    
    /**
     * Accessor for the current pickup floor.
     * 
     * @return the pickup floor, -1 if not on a pickup
     */
    @Override
    public int getPickupFloor(){
        return pickupFloor;
    }
    
    
    /**
     * Get the number of riders in the elevator.
     * 
     * @return the number of riders in the elevator
     */
    @Override
    public int getRiderCount(){
        return passengerCount;
    }
    
    
    /**
     * Get the direction of travel.
     * 
     * @return '0' for idle, '-1' for down, and '1' for up
     */
    @Override
    public int getDirection(){
        return direction;
    }
    
    
    /**
     * Accessor for the elevators maximum capacity.
     * 
     * @return the number of people the elevator can successfully hold at one time
     */
    @Override
    public int getMaxCapacity(){
        return maxCapacity;
    }
    
   
    /**
     * Method to shut down the elevator.
     */
    @Override
    public void stop(){
        running = false;
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
        HashSet<Person> toRemove = new HashSet();
        synchronized(getRiders()){
            for (Person p : getRiders()){
                if (p.getDestination() == getCurrentFloor()){
                    toRemove.add(p);
                    decrRiderCnt();
                }
            }
            for (Person p : toRemove){
                getRiders().remove(p);
                LogMgr.elevatorExitLog(getCurrentFloor(), p.getPersonNumber(), getElevNum(), getRiders());
                p.getOffElevator(f);
            }
        }
    }
    
    
    /**
     * Method to load a person onto the elevator.
     * 
     * @param p person to load
     */
    @Override
    public void load(Person p){
        incrRiderCnt();
        synchronized(getRiders()){
            getRiders().add(p);
        }
        LogMgr.elevatorBoardedLog(getCurrentFloor(), p.getPersonNumber(), getElevNum(), getRiders());
    }
    
        
    /**
     * Private method to increment the rider count when a new person gets on the elevator.
     */
    private void incrRiderCnt(){
        passengerCount++;
    }
       
    
    /**
     * Private method to decrement the rider count when a rider gets off the elevator. 
     */
    private void decrRiderCnt(){
        passengerCount--;
    }
    
    
    /**
     * Checks to see if the elevator has any stops to make.
     * 
     * @return true if there are stops to make
     */
    private boolean stopsRemain(){
        synchronized(getFloorRequests()){
            for (int i = 0; i < getFloorRequests().length; i++){
                if (getFloorRequests()[i] || getButtonPanel()[i]){
                    return true;
                }
            }
            return false;
        }
    }
    
    
    /**
     * Private method to operate the doors when elevator makes a stop.
     */
    private void doorAction() throws InvalidParameterException{
        LogMgr.doorsOpenLog(getElevNum(), getCurrentFloor());
        if (onPickup()){
            if (getPickupDir() == DOWN){
                Building.alertFloor(this, getCurrentFloor(), DOWN);
            }
            else if (getPickupDir() == UP){
                Building.alertFloor(this, getCurrentFloor(), UP);
            }
        }
        else{
            if (getDirection() == UP){
                Building.alertFloor(this, getCurrentFloor(), UP);
            }
            else if (getDirection() == DOWN){
                Building.alertFloor(this, getCurrentFloor(), DOWN);
            }
            else{
                Building.alertFloor(this, getCurrentFloor(), IDLE);
            }
        }
        synchronized(this){
            try {
                Thread.sleep(getDoorTime());
            } 
            catch (InterruptedException e){
                System.out.println("Door time interrupted by interrupted exception.  " + e.getMessage());
            }
        }
        LogMgr.doorsCloseLog(getElevNum(), getCurrentFloor());
    }
    
    
    /**
     * Private method to move the elevator in the up direction while there are stops to make.
     */
    private void moveUp() throws InvalidParameterException{
        Controller.scanReqs(getElevNum());
        while(stopsRemain() && getDirection() == UP){
            if (getFloorRequests()[getCurrentFloor()-1] || getButtonPanel()[getCurrentFloor()-1]){
                if (getButtonPanel()[getCurrentFloor()-1]){
                    LogMgr.riderArrivalLog(getElevNum(), getCurrentFloor());
                }
                if (getFloorRequests()[getCurrentFloor()-1]){
                    LogMgr.floorArrivalLog(getElevNum(), getCurrentFloor(), getDirection());
                }
                getFloorRequests()[getCurrentFloor()-1] = false;
                getButtonPanel()[getCurrentFloor()-1] = false;
                if (!stopsRemain()){
                    setDirection(IDLE);
                }
                doorAction();
                if (onPickup() && getCurrentFloor() == getPickupFloor()){
                    setPickupFloor(-1);
                    turnOffPickup();
                }
            }
            if (!stopsRemain() || getDirection() != UP){
                break;
            }
            LogMgr.elevatorUpLog(getCurrentFloor(), getElevNum(), getButtonPanel(), getFloorRequests(), getRiders());
            try {
                Thread.sleep(getElevatorSpeed());
                incFloor();
            } 
            catch (InterruptedException e) {
                System.out.println("Move up interrupted by interrupted exception.  " + e.getMessage());
            }
        }
        synchronized(this){
            if (!stopsRemain() && getDirection() == IDLE){
                LogMgr.noRequestsLog(getElevNum(), getButtonPanel(), getFloorRequests());
                Controller.elevIdle(getElevNum());
            }
        }
    }
    
    
    /**
     * Private method to move the elevator down while there are stops to make.
     */
    private void moveDown() throws InvalidParameterException{
        Controller.scanReqs(getElevNum());
        while(stopsRemain() && getDirection() == DOWN){
            if (getFloorRequests()[getCurrentFloor()-1] || getButtonPanel()[getCurrentFloor()-1]){
                if (getButtonPanel()[getCurrentFloor()-1]){
                    LogMgr.riderArrivalLog(getElevNum(), getCurrentFloor());
                }
                if (getFloorRequests()[getCurrentFloor()-1]){
                    LogMgr.floorArrivalLog(getElevNum(), getCurrentFloor(), getDirection());
                }
                getFloorRequests()[getCurrentFloor()-1] = false;
                getButtonPanel()[getCurrentFloor()-1] = false;
                if (!stopsRemain()){
                    setDirection(IDLE);
                }
                doorAction();
                if (onPickup() && getCurrentFloor() == getPickupFloor()){
                    setPickupFloor(NO_SUCH_FLOOR);
                    turnOffPickup();
                }
            }
            if (!stopsRemain() || getDirection() != DOWN){
                break;
            }
            LogMgr.elevatorDownLog(getCurrentFloor(), getElevNum(), getButtonPanel(), getFloorRequests(), getRiders());
            try {
                Thread.sleep(getElevatorSpeed());
                decFloor();
            } 
            catch (InterruptedException e) {
                System.out.println("Move down interrupted by interrupted exception.  " + e.getMessage());
            }
        }
        synchronized(this){
            if (!stopsRemain() && getDirection() == IDLE){
                LogMgr.noRequestsLog(getElevNum(), getButtonPanel(), getFloorRequests());
                Controller.elevIdle(getElevNum());
            }
        }
    }
        
    
    /**
     * Private method to decide the elevator's direction of travel and set the appropriate direction.
     */
    private void move() throws InvalidParameterException{
        int firstReq = 0;
        synchronized(getFloorRequests()){
            for (int i = 0; i < getButtonPanel().length; i++){
                if (getButtonPanel()[i] || getFloorRequests()[i]){
                    firstReq = i + 1;
                    break;
                }
            }
        }
        if (firstReq < getCurrentFloor()){
            setDirection(DOWN);
            moveDown();
        }
        else if (firstReq > getCurrentFloor()){
            setDirection(UP);
            moveUp();
        }
        if (stopsRemain()){
            move();
        }        
    }
    
    /**
     * Mutator for setting the elevators direction of travel.
     * 
     * @param dir the intended direction of travel to set
     * 
     * @throws InvalidParameterException if the integer is not a valid direction
     */
    private void setDirection(int dir){
        if (dir == DOWN){
            direction = DOWN;
        }
        else if (dir == UP){
            direction = UP;
        }
        else if (dir == IDLE){
            direction = IDLE;
        }
    }
    
    
    /**
     * Decreases value of current floor when moving down.
     */
    private void decFloor(){
        currentFloor--;
    }
    
    
    /**
     * Increases value of current floor when moving up.
     */
    private void incFloor(){
        currentFloor++;
    }
    
    
    /**
     * Accessor for the speed of an elevator floor to floor.
     * 
     * @return the milliseconds between floors
     */
    private int getElevatorSpeed(){
        return elevatorSpeed;
    }
    
    
    /**
     * Accessor for the button panel of the elevator.
     * 
     * @return the button panel array
     */
    private boolean[] getButtonPanel(){
        return buttonPanel;
    }
    
    
    /**
     * Accessor for the floor requests of the elevator.
     * 
     * @return the floor request array
     */
    private boolean[] getFloorRequests(){
        return floorRequests;
    }
    
    
    /**
     * Accessor for the riders of the elevator.
     *
     * @return the array list of riders
     */
    private ArrayList<Person> getRiders(){
        return riders;
    }
    
    
    /**
     * Mutator for the pickup floor value.
     * @param floorNum the floor number to set the pickup floor to
     */
    private void setPickupFloor(int floorNum){
        pickupFloor = floorNum;
    }
    
    
    /**
     * Mutator to turn off pickup boolean by setting it to false.
     */
    private void turnOffPickup(){
        onPickup = false;
        pickupDir = IDLE;
    }
    
    
    /**
     * Accessor for the door time of the elevator.
     * 
     * @return the milliseconds that the door takes to open and close
     */
    private int getDoorTime(){
        return doorTime;
    }
    
    
    /**
     * Accessor for the idle time before the elevator times out and returns to default floor.
     * 
     * @return the milliseconds before the timeout occurs
     */
    private int getIdleTime(){
        return idleTime;
    }
    
    
    /**
     * Accessor for the default floor of the elevator.
     * 
     * @return the floor number for the default floor
     */
    private int getDefaultFloor(){
        return defaultFloor;
    }
    
    
    /**
     * Mutator to start the elevator running.
     */
    private void startRunning(){
        running = true;
    }
    
    /**
     * Mutator to set the current floor upon elevator creation.
     * 
     * @param floorNum the number of the floor to set the current floor to
     */
    private void setCurrentFloor(int floorNum){
        currentFloor = floorNum;
    }
    
    
    /**
     * Mutator to set the passenger count upon elevator creation.
     * 
     * @param numPassengers the number of passengers to set
     */
    private void setPassengerCount(int numPassengers){
        passengerCount = numPassengers;
    }
    
    
    /**
     * Accessor to get the status of the elevator.
     * 
     * @return true if the elevator is running
     */
    private boolean getRunning(){
        return running;
    }
    
    
    /**
     * Mutator to set the list of riders.
     * 
     * @param people the list of person objects to set to
     */
    private void setRiders(ArrayList<Person> people){
        riders = people;
    }
}
