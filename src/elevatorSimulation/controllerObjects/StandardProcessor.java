package elevatorSimulation.controllerObjects;

import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.utility.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Original processor for pending requests.  Implements standard processing for pending requests and manages pending requests.
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see java.util.ArrayList
 * @see java.util.HashSet
 * @see java.util.LinkedList
 */
public class StandardProcessor implements PendingReqsProcessor{
    
    /**
     * List of elevators to process pending requests for.
     */
    private final ArrayList<Elevator> elevators;
    
    /**
     * List of pending requests.
     */
    private static LinkedList<Node> pendingReqs;
    
    /**
     * Constant representing the direction up.
     */
    private static final int UP = 1;
    
    /**
     * Constant representing the direction down.
     */
    private static final int DOWN = -1;
    
    
    /**
     * Private inner class to create a node for a pending request.  The node gets used in the linked list which is 
     * the pending requests queue.
     */
    private class Node{
        private final int floor;
        private final int direction;
        private Node next;
        private Node(int flr, int dir) { floor = flr; direction = dir; next = null; }
        private Node(int flr, int dir, Node ptr) { floor = flr; direction = dir; next = ptr; }
        private int getDirection(){ return direction; }
        private int getFloor(){ return floor; }
    }

    
    /**
     * Constructor to build a new pending request processor of type standard.
     * 
     * @param elevs array list of elevators to give pending requests to
     */
    public StandardProcessor(ArrayList<Elevator> elevs) {
        elevators = elevs;
        setPendingReqs(new LinkedList());
    }
    
    
    /**
     * Method to add a request to the queue of pending requests.
     * 
     * @param flrNum integer representing the floor number that the floor request is for
     * @param dir integer representing the direction of the request, 1 for up, -1 for down
     */
    @Override
    public void addPendReq(int flrNum, int dir){
        synchronized(getPendingReqs()){
            getPendingReqs().add(new Node(flrNum, dir));
        }
    }
    
    
    /**
     * Method to begin a pickup if there are any pending requests for an idle elevator.
     * 
     * @param eNum integer representing an elevator that is requesting a new pickup
     * 
     * @throws InvalidParameterException if the exception is thrown up the call stack
     */
    @Override
    public void beginPickup(int eNum) throws InvalidParameterException{
        if (getPendingReqs().isEmpty()){
            return;
        }
        int flrNum, dir;
        synchronized(getPendingReqs()){
            if (getPendingReqs().isEmpty()){
                return;
            }
            Node tempNode = getPendingReqs().removeFirst();
            flrNum = tempNode.getFloor();
            dir = tempNode.getDirection();
            Elevator e = getElevators().get(eNum-1);
            e.setPickup(flrNum, dir);
            e.addFlrReq(flrNum);
        }
    }
    
    
    /**
     * Method called to check pending requests if an elevator begins movement.
     * 
     * @param eNum number of the elevator that is starting movement and asking for any requests it can take
     * 
     * @throws InvalidParameterException if the exception is thrown up the call stack
     */
    @Override
    public void checkPending(int eNum) throws InvalidParameterException{
        Elevator e = getElevators().get(eNum-1);
        if (getPendingReqs().isEmpty()){
            return;
        }
        int dir = e.getDirection();
        synchronized(getPendingReqs()){
            HashSet<Node> toRemove = new HashSet();
            for (Node n : getPendingReqs()){
                Node tn = n;
                if (e.onPickup()){
                    if (e.getCurrentFloor() < tn.getFloor() && tn.getDirection() == UP && e.getPickupDir() == UP && e.getDirection() == UP){
                        if (tn.getFloor() > e.getPickupFloor()){
                            e.setPickup(tn.getFloor(), UP);
                        }
                        e.addFlrReq(tn.getFloor());
                        toRemove.add(tn);
                    }
                    else if (e.getCurrentFloor() > tn.getFloor() && tn.getDirection() == DOWN && e.getPickupDir() == DOWN && e.getDirection() == DOWN){
                        if (tn.getFloor() < e.getPickupFloor()){
                            e.setPickup(tn.getFloor(), DOWN);
                        }
                        e.addFlrReq(tn.getFloor());
                        toRemove.add(tn);
                    }
                }
                else{
                    if (tn.getDirection() == UP && dir == UP){
                        if (tn.getFloor() > e.getCurrentFloor()){
                            e.setPickup(tn.getFloor(), UP);
                            e.addFlrReq(tn.getFloor());
                            toRemove.add(tn);
                        }
                    }
                    else if (tn.getDirection() == DOWN && dir == DOWN){
                        if (tn.getFloor() < e.getCurrentFloor()){
                            e.setPickup(tn.getFloor(), DOWN);
                            e.addFlrReq(tn.getFloor());
                            toRemove.add(tn);
                        }
                    }
                }
            }
            for (Node n : toRemove){
                getPendingReqs().remove(n);
            }
        }
    }
    
    
    /**
     * Accessor for the list of pending requests.
     * 
     * @return the linked list of pending requests
     */
    private LinkedList<Node> getPendingReqs(){
        return pendingReqs;
    }
    
    
    /**
     * Accessor for the list of elevators.
     * 
     * @return the array list of elevators
     */
    private ArrayList<Elevator> getElevators(){
        return elevators;
    }
    
    
    /**
     * Mutator for the pending requests list.
     * 
     * @param pR the pending requests list to set to
     */
    private void setPendingReqs(LinkedList<Node> pR){
        pendingReqs = pR;
    }
}
