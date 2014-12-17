package elevatorSimulation.controllerObjects;

import elevatorSimulation.elevatorObjects.Elevator;
import java.util.ArrayList;

/**
 * Standard elevator selector impl.  Controller delegates to this class to decide the appropriate elevator to give a new request to.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see java.util.ArrayList
 */
public class StandardSelector implements ElevatorSelector {
    
    /**
     * Array list of elevators to get elevator information.
     */
    private final ArrayList<Elevator> elevators;
    
    /**
     * Constant representing the direction down.
     */
    private static final int DOWN = -1;
    
    /**
     * Constant representing the direction up.
     */
    private static final int UP = 1;
    
    /**
     * Constant representing no direction of travel.
     */
    private static final int IDLE = 0;
    

    /**
     * Constructor for a new standard selector.  Takes an array list of elevators in order to check each elevator's status 
     * for selection.
     * 
     * @param elevs the array list of elevators that the controller controls
     */
    public StandardSelector(ArrayList<Elevator> elevs) {
        elevators = elevs;
    }
    
    
    /**
     * Selects the appropriate elevator to give a new request to.
     * 
     * @param floorNum the floor number the request is coming from
     * @param direction the direction the request is for, 1 for up, -1 for down
     * 
     * @return the elevator number that the selector found to take the request, -1 if no elevator is currently available
     */
    @Override
    public int selectElevator(int floorNum, int direction){
        for (Elevator e : getElevators()){
            if (e.onPickup()){
                if (direction == DOWN){
                    if (e.getPickupDir() == DOWN && e.getDirection() == DOWN && e.getCurrentFloor() > floorNum){
                        if (floorNum < e.getPickupFloor()){
                            e.setPickup(floorNum, DOWN);
                        }
                        return e.getElevNum();
                    }
                }
                else{
                    if (e.getPickupDir() == UP && e.getDirection() == UP && e.getCurrentFloor() < floorNum){
                        if (floorNum > e.getPickupFloor()){
                            e.setPickup(floorNum, UP);
                        }
                        return e.getElevNum();
                    }
                }
            }
        }
        for (Elevator e : getElevators()){
            if (!e.onPickup() && e.getDirection() != IDLE){
                if (direction == UP){
                    if (e.getDirection() == UP && e.getCurrentFloor() < floorNum){
                        e.setPickup(floorNum, UP);
                        return e.getElevNum();
                    }
                }
                else{
                    if (e.getDirection() == DOWN && e.getCurrentFloor() > floorNum){
                        e.setPickup(floorNum, DOWN);
                        return e.getElevNum();
                    }
                }
            }
        }
        for (Elevator e : getElevators()){
            if (!e.onPickup() && e.getDirection() == IDLE){
                e.setPickup(floorNum, direction);
                return e.getElevNum();
            }
        }
        return -1;
    }
    
    
    /**
     * Accessor for the list of elevators.
     * 
     * @return the array list of elevators for the selection process
     */
    private ArrayList<Elevator> getElevators(){
        return elevators;
    }
}
