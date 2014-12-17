package elevatorSimulation.utility;

import elevatorSimulation.buildingObjects.Person;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * A class to manage logs for the elevator simulation.  Has numerous methods to print output when particular events occur.
 * 
 * @author Brandon Pauly
 */
public class LogMgr {
    
    /**
     * First time stamp to measure all subsequent timestamps to.
     */
    private static long firstStamp;
    
    /**
     * Time stamp formatter;
     */
    private static SimpleDateFormat format;
    
    /**
     * Hours of simulation operation.
     */
    private static long hours;
     
    
    /**
     * Prints log for building creation.
     * 
     * @param floors number of floors
     * @param elevators number of elevators
     */
    public static void buildingCreationLog(int floors, int elevators){
        firstStamp = System.currentTimeMillis();
        format = new SimpleDateFormat(String.format("%02d",hours) + ":mm:ss.SSS");
        System.out.println(timeStamp() + "\tBuilding created with " + floors + " floors and " + elevators + " elevators");
    }
    
    
    /**
     * Prints a log when an elevator moves up.
     * 
     * @param floorNumber floor number of the elevator
     * @param elevatorNumber identifying number of the elevator 
     * @param riderRequests the rider requests for the elevator
     * @param floorRequests the floor requests for the elevator
     * @param riders list of riders that are on the elevator
     */
    public static void elevatorUpLog(int floorNumber, int elevatorNumber, boolean[] riderRequests, boolean[] floorRequests, ArrayList<Person> riders){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " moving up from floor " + floorNumber + " to floor " + (++floorNumber) 
                + " [Rider Requests: " + requestMaker(riderRequests) + "] [Floor Requests: " + requestMaker(floorRequests) + "] [Riders: " 
                + passengerMaker(riders) + "]");
    }
    
    
    /**
     * Prints a log when an elevator moves down.
     * 
     * @param floorNumber floor number of the elevator
     * @param elevatorNumber identifying number of the elevator 
     * @param riderRequests the rider requests for the elevator
     * @param floorRequests the floor requests for the elevator
     * @param riders list of riders that are on the elevator
     */
    public static void elevatorDownLog(int floorNumber, int elevatorNumber, boolean[] riderRequests, boolean[] floorRequests, ArrayList<Person> riders){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " moving down from floor " + floorNumber + " to floor " + (--floorNumber) 
                + " [Rider Requests: " + requestMaker(riderRequests) + "] [Floor Requests: " + requestMaker(floorRequests) + "] [Riders: " 
                + passengerMaker(riders) + "]");
    }
    
    
    /**
     * Prints a log when an elevator arrives for a rider request.
     * 
     * @param elevatorNumber identifying number of the elevator
     * @param floorNumber floor number of the elevator
     */
    public static void riderArrivalLog(int elevatorNumber, int floorNumber){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " arrived at floor " + floorNumber + " for rider stop");
    }
    
    
    /**
     * Prints a log when an elevator arrives for a floor request.
     * 
     * @param elevatorNumber identifying number of the elevator
     * @param floorNumber floor number of the elevator
     * @param direction number representing the direction of travel
     */
    public static void floorArrivalLog(int elevatorNumber, int floorNumber, int direction){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " arrived at floor " + floorNumber + " for floor request.");
    }
    
    
    /**
     * Prints a log when a person is placed on a floor from an elevator.
     * 
     * @param flrNum the floor the person is entering
     * @param pNum the identifier for the person
     * @param people the array list of people on the floor after entry
     */
    public static void floorEntryLog(int flrNum, int pNum, ArrayList people){
        System.out.println(timeStamp() + "\tPerson " + pNum + " entered floor " + flrNum + " People: [" + passengerMaker(people) + "].");
    }
    
    
    /**
     * Prints a log when a person is exiting a floor onto an elevator for travel.
     * 
     * @param flrNum the floor the person is exiting
     * @param pNum the identifier of the person
     * @param people the array list of people on the floor after entry
     */
    public static void floorExitLog(int flrNum, int pNum, ArrayList people){
        System.out.println(timeStamp() + "\tPerson " + pNum + " has left floor " + flrNum + " People: [" + passengerMaker(people) + "].");
    }
    
    
    /**
     * Prints a log for the doors opening.
     * 
     * @param elevatorNumber number identifying the elevator
     * @param floorNumber floor number the elevator is on
     */
    public static void doorsOpenLog(int elevatorNumber, int floorNumber){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " doors opened on floor " + floorNumber);
    }
    
    
    /**
     * Prints a log for the doors closing.
     * 
     * @param elevatorNumber number identifying the elevator
     * @param floorNumber floor number the elevator is on
     */
    public static void doorsCloseLog(int elevatorNumber, int floorNumber){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " doors closed on floor " + floorNumber);
    }
    
    
    /**
     * Prints a log when the up button is pressed on a floor.
     * 
     * @param floorNumber floor number that the button is pressed on
     * @param pNum identifier of person pressing the up button
     */
    public static void upPressed(int floorNumber, int pNum){
        System.out.println(timeStamp() + "\tUp button pressed on floor " + floorNumber + " by person " + pNum + ".");
    }
    
    /**
     * Prints a log when the down button is pressed on a floor.
     * 
     * @param floorNumber floor number that the button is pressed on
     * @param pNum identifier of person pressing the down button
     */
    public static void downPressed(int floorNumber, int pNum){
        System.out.println(timeStamp() + "\tDown pressed on floor " + floorNumber + " by person " + pNum + ".");
    }
    /**
     * Prints a log when an elevator has no stops to make.
     * 
     * @param elevatorNumber number identifying the elevator
     * @param riderRequests array of rider requests
     * @param floorRequests array of floor requests
     */
    public static void noRequestsLog(int elevatorNumber, boolean[] riderRequests, boolean[] floorRequests){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " has no requests [Rider Requests: " + requestMaker(riderRequests) + 
                "] [Floor Requests: " + requestMaker(floorRequests) + "]");
    }
    
    
    /**
     * Prints a log when an elevator times out and goes to default floor.
     * 
     * @param elevatorNumber identifying number for the elevator
     * @param defaultFloor default floor number
     */
    public static void defaultFloorLog(int elevatorNumber, int defaultFloor){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " timed out.  Returning to default floor: " + defaultFloor);
    }
    
    
    /**
     * Prints a log when a rider request is added.
     * 
     * @param elevatorNumber identifying number for the elevator
     * @param floorNumber floor number the request is for
     * @param riderRequests rider requests for outputting rider reqs
     * @param floorRequests floor requests for outputting floor reqs
     */
    public static void riderReqAddedLog(int elevatorNumber, int floorNumber, boolean[] riderRequests, boolean[] floorRequests){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " button " + floorNumber + " pushed, [Rider Requests: " + 
                requestMaker(riderRequests) + "] [Floor Requests: " + requestMaker(floorRequests) + "].");
    }
    
    
    /**
     * Prints a log when a floor request is added.
     * 
     * @param elevatorNumber identifying number for the elevator
     * @param floorNumber floor number the request is for
     * @param riderRequests rider requests for outputting rider reqs
     * @param floorRequests floor requests for outputting floor reqs
     */
    public static void floorReqAddedLog(int elevatorNumber, int floorNumber, boolean[] riderRequests, boolean[] floorRequests){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " received request for floor " + floorNumber + ", " + 
                "[Rider Requests: " + requestMaker(riderRequests) + "] [Floor Requests: " + requestMaker(floorRequests) + "].");
    }
    
    
    /**
     * Prints a log when there is a rider request that cannot be fulfilled.
     * 
     * @param elevatorNumber identifying number for the elevator
     * @param floorNumber floor number the request was made for
     */
    public static void improperRiderReqLog(int elevatorNumber, int floorNumber){
        System.out.println(timeStamp() + "\tElevator " + elevatorNumber + " rider request made for floor " + floorNumber + " - WRONG DIRECTION");
    }
    
    
    /**
     * Prints a log when a person boards an elevator.
     * 
     * @param srcFloor the source floor number the person is boarding from
     * @param personNumber the identifier for the person boarding
     * @param elevatorNumber the elevator number of the elevator the person is boarding
     * @param riders list of riders on the elevator
     */
    public static void elevatorBoardedLog(int srcFloor, int personNumber, int elevatorNumber, ArrayList riders){
        System.out.println(timeStamp() + "\tPerson " + personNumber + " boarded elevator " + elevatorNumber + " from floor " + srcFloor + ", Riders: [" 
                + passengerMaker(riders) + "].");
    }
    
    
    /**
     * Prints a log when a person exits an elevator.
     * 
     * @param flrNum the floor number that the person is exiting onto
     * @param personNumber the identifier for the person exiting the elevator
     * @param elevatorNumber the elevator number of the elevator the person is exiting
     * @param riders list of riders on the elevator
     */
    public static void elevatorExitLog(int flrNum, int personNumber, int elevatorNumber, ArrayList riders) {
        System.out.println(timeStamp() + "\tPerson " + personNumber + " exited elevator " + elevatorNumber + " onto floor " + flrNum + ", Riders: [" 
                + passengerMaker(riders) + "].");
    }
    
    
    /**
     * Prints a log containing appropriate data when a person is created.
     * 
     * @param personNumber the identifying number for the person being created
     * @param sourceFloor the floor number the person is being created on
     * @param destFloor the floor number the person will be traveling to
     */
    public static void personCreationLog(int personNumber, int sourceFloor, int destFloor){
        if (sourceFloor > destFloor){
            System.out.println(timeStamp() + "\tPerson " + personNumber + " created on floor " + sourceFloor + " destined to travel DOWN to floor " + 
                    destFloor + ".");
        }
        else {
             System.out.println(timeStamp() + "\tPerson " + personNumber + " created on floor " + sourceFloor + " destined to travel UP to floor " + 
                    destFloor + ".");
        }
    }
    

    /**
     * Creates a string to time stamp with.
     * 
     * @return a string that is a time stamp
     */
    private static String timeStamp(){
        hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - firstStamp);
        return format.format(System.currentTimeMillis() - firstStamp);
    }
    
    
    /**
     * Makes a string of requests that can be printed from a boolean array.
     * 
     * @param requests the array to be created into a string representation
     * 
     * @return a string representing the requests from the array
     */
    private static String requestMaker(boolean[] requests){
        ArrayList<Integer> reqString = new ArrayList();
        for (int i = 0; i < requests.length; i++) {
            if (requests[i]){
                reqString.add(i + 1);
            }      
        }
        if (reqString.isEmpty()){
            return "None ";
        }
        return reqString.toString();
    }
    
    
    /**
     * Method to construct a string representing riders on an elevator.
     * 
     * @param riders the array list of riders
     * 
     * @return string displaying all of the riders on an elevator
     */
    private static String passengerMaker(ArrayList<Person> riders){
        String passengers = "";
        int size = riders.size();
        if (size == 0){
            return "None";
        }
        for (int i = 0; i < size-1; i++){
            passengers += "P" + riders.get(i).getPersonNumber() + ", ";
        }
        passengers += "P" + riders.get(size-1).getPersonNumber();
        return passengers;
    }

    
    /**
     * Private constructor to ensure there is no instantiation of a LogMgr object.
     */
    private LogMgr(){}
}


