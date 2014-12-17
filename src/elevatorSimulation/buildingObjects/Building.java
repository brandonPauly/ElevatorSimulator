package elevatorSimulation.buildingObjects;

import elevatorSimulation.controllerObjects.Controller;
import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.utility.DataProcessor;
import elevatorSimulation.utility.InvalidParameterException;
import elevatorSimulation.utility.LogMgr;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class represents a building in the elevator simulation with floors and the elevator controller.  
 * Building is a singleton class.  It owns the elevator controller and creates the floors.  
 * 
 * @author Brandon Pauly
 * 
 * @see java.util.ArrayList
 * @see elevatorSimulation.utility.LogMgr
 * @see elevatorSimulation.controllerObjects.Controller
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see java.util.Random
 */
public final class Building {
    
    /*
     * An array list to hold Floor objects within the building.
     */
    private static ArrayList<Floor> floors; 
    
    /*
     * A static variable to hold the instance for the Building class.
     */
    private volatile static Building buildingInstance;
    
    /**
     * A data member to the elevator controller for the building.
     */
    private static Controller controller;
    
    /*
     * The integer value that represents the direction up: {@value}
     */
    private static final int UP = 1;
    
    /*
     * The integer value that represents the direction down: {@value}
     */
    private static final int DOWN = -1;
    
    /*
     * Factory thread to call the create people method at particular intervals based on the people per minuter requested.
     */
    private final PeopleTimer peopleFactory;
    
    /*
     * Random number generator to generate a random number for source floors and destination floors for people production.
     */
    private static Random random;
    
    /**
     * Array list of all people in the building.
     */
    private static ArrayList<Person> people;
    
    

    
 
    /**
     * Singleton method to return the building instance.  If the building instance is null, then the constructor is called to construct the
     * building instance.  Otherwise the method returns the first instance created.
     * 
     * @param selector string representing the type of selector impl to use for elevator selection
     * @param processor string representing the type of processor impl to use for processing pending requests
     * @param elevatorType string representing the type of elevator to install in the building
     * @param floors integer representing the number of floors that make up the building
     * @param elevatorQuantity integer representing the number of elevators to install in the building
     * @param doorTime integer representing the time, in milliseconds, that the doors of all elevators open on each floor
     * @param elevatorSpeed integer representing the time, in milliseconds, that all elevators take to travel from floor to floor
     * @param dfltElevFlr integer representing the default floor for all elevators
     * @param elevTimeout integer representing the time, in milliseconds, before an elevator times out and returns to its default floor
     * @param maxCapacity integer representing the maximum number of people that can ride an elevator
     * @param ppm integer representing the number of people to create per minute during simulator production
     * @param duration integer representing the number of minutes for which people should be created
     * 
     * @return a reference to the single instance of Building
     * 
     * @throws InvalidParameterException if any parameters are invalid for object creation
     */
    public static Building getBuildingInstance(String selector, String processor, String elevatorType, int floors, int elevatorQuantity, 
            int doorTime, int elevatorSpeed, int dfltElevFlr, int elevTimeout, int maxCapacity, int ppm, int duration) throws InvalidParameterException {
        if (floors < 2 || elevatorQuantity < 1 || (dfltElevFlr < 1 || dfltElevFlr > floors) || (doorTime < 1 || elevatorSpeed < 1 || elevTimeout < 1 ||
                maxCapacity < 1 || ppm < 1 || duration < 1)){
            throw new InvalidParameterException("Invalid parameter passed for building creation.");
        }
        if (getBuildingInstance() == null){
            synchronized(Building.class){
                if (getBuildingInstance() == null){
                    setBuildingInstance(new Building(selector, processor, elevatorType, floors, elevatorQuantity, doorTime, elevatorSpeed, 
                            dfltElevFlr, elevTimeout, maxCapacity, ppm, duration));
                }
            }
        }
        return getBuildingInstance();
    }
    
    /**
     * Alerts a particular floor that an elevator has arrived for either an up request, a down request, or an idle situation.
     * 
     * @param elevator the elevator that the has arrived at the floor
     * @param floorNumber integer for the floor number, of which to alert 
     * @param direction integer representing the direction of travel, 1 for up, -1 for down
     * 
     * @throws elevatorSimulation.utility.InvalidParameterException if upArrival, downArrival, or idleUnload have thrown the exception
     * 
     */
    public static void alertFloor(Elevator elevator, int floorNumber, int direction) throws InvalidParameterException{
        if (direction == UP){
            getFloors().get(floorNumber-1).upArrival(elevator);
        }
        else if (direction == DOWN){
            getFloors().get(floorNumber-1).downArrival(elevator);
        }
        else{
            getFloors().get(floorNumber-1).idleUnload(elevator);
        }
    }
    
    /**
     * Method to give a floor call to the elevator controller when someone presses the up or down button on a floor.
     * 
     * @param floorNumber integer representing the floor number that the floor call is for
     * @param direction integer representing the direction of travel, 1 for up, -1 for down
     * 
     * @throws InvalidParameterException if addFloorRequest throws the exception
     */
    public static void giveFloorCall(int floorNumber, int direction) throws InvalidParameterException{
        getController().addFloorRequest(floorNumber, direction);
    }
    
    
    /**
     * Method to inform elevators to finish up and stop, once people production has ended.
     */
    public static void endPplProd(){
        getController().stopElevators();
    }
    
    
    /**
     * Method to create a person object and place them on a random floor, with a random destination.
     * 
     * @param identifier integer representing the number for which to identify the person being created
     * 
     * @throws InvalidParameterException if it is thrown up the stack to Floor.load(Person)
     */
    public static void createPerson(int identifier) throws InvalidParameterException{
        int destination = 0;
        int sourceFlrNum = getRandom().nextInt(getFloors().size());
        while (destination == (sourceFlrNum + 1) || destination < 1){
            destination = getRandom().nextInt(getFloors().size()) + 1;
        }
        Floor srcFlr = getFloors().get(sourceFlrNum);
        Person p = new Person(identifier, srcFlr, destination);
        getPeople().add(p);
        srcFlr.load(p);
        LogMgr.personCreationLog(p.getPersonNumber(), srcFlr.getFlrNum(), p.getDestination());
        if (destination > sourceFlrNum){
            srcFlr.pressUp(identifier);
        }
        else{
            srcFlr.pressDown(identifier);
        }
    }
    
    /**
     * Passes people list to data processor to process data and create tables for output.
     */
    public static void simulationFinished(){
        DataProcessor.processPersonData(getNumFloors(), getPeople());
    }
    
    
    /**
     * Private constructor to build a new Building object.  This constructor is private per the singleton design pattern.
     * 
     * @param elevatorType string representing the type of elevator to install in the building
     * @param people integer representing the number of people to be inserted into the building
     * @param floors integer representing the number of floors that make up the building
     * @param elevatorQuantity integer representing the number of elevators to install in the building
     * @param doorTime integer representing the time, in milliseconds, that the doors of all elevators open on each floor
     * @param elevatorSpeed integer representing the time, in milliseconds, that all elevators take to travel from floor to floor
     * @param dfltElevFlr integer representing the default floor for all elevators
     * @param elevTimeout integer representing the time, in milliseconds, before an elevator times out and returns to its default floor
     * @param maxCapacity integer representing the maximum number of people that can ride an elevator
     * 
     * @throws InvalidParameterException if any of the parameters are invalid for object creation
     */
    private Building(String selector, String processor, String elevatorType, int flrQty, int elevatorQuantity, int doorTime, int elevatorSpeed, 
            int dfltElevFlr, int elevTimeout, int maxCapacity, int peoplePerMin, int duration) throws InvalidParameterException {
        setFloors(new ArrayList()); 
        setPeople(new ArrayList());
        createFloors(flrQty);
        setRandom(new Random());
        setController(Controller.getControllerInstance(selector, processor, elevatorType, elevatorQuantity, doorTime, elevatorSpeed, flrQty, dfltElevFlr, 
                elevTimeout, maxCapacity));
        LogMgr.buildingCreationLog(flrQty, elevatorQuantity);
        peopleFactory = PeopleTimer.getTimerInstance(peoplePerMin, duration);
        startPeopleProduction();
    }
    
    
    /**
     * Creates floor objects that are housed within the building and places them into the floors ArrayList.
     * 
     * @param  quantity number of floors to create
     * 
     * @throws InvalidParameterException if building is created with less than 2 floors
     */
    private void createFloors(int quantity) throws InvalidParameterException {
        for (int i = 0; i < quantity; i++){
            Floor f = new Floor(i + 1);
            getFloors().add(f);
        }
    }    
    
    
    /**
     * Private method called in the constructor to begin people production.  People production is managed by a separate thread
     * which keeps track of time and calls the function to create a person.
     */
    private void startPeopleProduction(){
        getPeopleFactory().makePeople();
    }
    
    
    /**
     * Private method to get the list of people within the building.
     * @return array list of all people in the building
     */
    private static ArrayList getPeople(){
        return people;
    }
    
    
    /**
     * Accessor for the number of floors in the building.
     * 
     * @return the number of floors in the building 
     */
    private static int getNumFloors(){
        return floors.size();
    }
    
    
    /**
     * Accessor for the people factory that times people creation.
     * 
     * @return the people factory instance 
     */
    private PeopleTimer getPeopleFactory(){
        return peopleFactory;
    }
    
    
    /**
     * Accessor for the floors list.
     * 
     * @return the array list of floors
     */
    private static ArrayList<Floor> getFloors(){
        return floors;
    }
    
    /**
     * Accessor for the controller instance.
     * 
     * @return the controller for the elevators 
     */
    private static Controller getController(){
        return controller;
    }
    
    /**
     * Accessor for the random number generator.
     * 
     * @return the random number generator for people creation
     */
    private static Random getRandom(){
        return random;
    }
    
    
    /**
     * Accessor for the building instance.
     * 
     * @return the current instance of building if there is one
     */
    private static Building getBuildingInstance(){
        return buildingInstance;
    }
    
    
    /**
     * Mutator to set the building instance when the first instance is created.
     * 
     * @param instance the building to create
     */
    private static void setBuildingInstance(Building instance){
        buildingInstance = instance;
    }
    
    
    /**
     * Mutator for the list of floors.
     * 
     * @param flrs the array list of floors to set to  
     */
    private void setFloors(ArrayList<Floor> flrs){
        floors = flrs;
    }
    
    
    /**
     * Mutator for the list of people in the building.
     * 
     * @param ppl the array list of people to set to 
     */
    private void setPeople(ArrayList<Person> ppl){
        people = ppl;
    }
    
    
    /**
     * Mutator to set the random number generator for people creation.
     * 
     * @param rand the random number generator to set to
     */
    private void setRandom(Random rand){
        random = rand;
    }
    
    
    /**
     * Mutator to set the controller for the elevators within the building.
     * 
     * @param c the controller to set to
     */
    private void setController(Controller c){
        controller = c;
    }
}
