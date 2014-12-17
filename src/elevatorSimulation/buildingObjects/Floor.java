package elevatorSimulation.buildingObjects;

import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.utility.InvalidParameterException;
import elevatorSimulation.utility.LogMgr;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class represents a floor within a building.  It holds people when they are not on an elevator and interacts with elevators and the 
 * building to facilitate the exchange of passengers.
 *
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.elevatorObjects.Elevator
 * @see elevatorSimulation.utility.InvalidParameterException
 * @see java.util.ArrayList
 * @see java.util.HashSet
 */
public final class Floor {
    
    /*
     * Array list to hold people when they are on a floor.
     */
    private ArrayList<Person> people;
    
    
    /*
     * The level of the floor within the building.
     */
    private final int floorNumber;
    
    /*
     * The up button for the floor.
     */
    private boolean upPressed;
    
    /*
     * The down button for the floor.
     */
    private boolean downPressed;
    
    /*
     * A constant representing the up direction.
     */
    private static final int UP = 1;
    
    /*
     * A constant representing the down direction.
     */
    private static final int DOWN = -1;
    
    
    /**
     * Constructor to construct a new floor object within the building.
     * 
     * @param flrNum the number that represents the level of the floor in the building
     * 
     * @throws elevatorSimulation.utility.InvalidParameterException if the floor is created with a floor number less than 1
     */
    public Floor(int flrNum) throws InvalidParameterException{
        if (flrNum < 1){
            throw new InvalidParameterException("Floor numbers must be greater than 0.");
        }
        setPeople(new ArrayList());
        floorNumber = flrNum;
        setUpPressed(false);
        setDownPressed(false);
    }
    
    
    /**
     * Method to set the up button to 'off' and begin the exchange of people when an elevator has arrived while going up.
     * 
     * @param elevator the elevator that has arrived for the up request
     * 
     * @throws elevatorSimulation.utility.InvalidParameterException if Person.getOnElevator() throws the exception
     */
    public void upArrival(Elevator elevator) throws InvalidParameterException {
        if (elevator == null){
            throw new InvalidParameterException("Elevator is null.  Cannot exchange passengers.");
        }
        setUpPressed(false);
        elevator.unloadRiders(this);
        HashSet<Person> toRemove = new HashSet();
        synchronized(getPeople()){
            for (Person p : getPeople()){
                if (p.getDestination() > getFlrNum() && elevator.getRiderCount() < elevator.getMaxCapacity()){
                    toRemove.add(p);
                }
            }
            for (Person p : toRemove){
                getPeople().remove(p);
                LogMgr.floorExitLog(getFlrNum(), p.getPersonNumber(), getPeople());
                p.getOnElevator(elevator);
            }
            for (Person p : getPeople()){
                if (p.getDestination() > getFlrNum()){
                    p.pressUp();
                    break;
                }
            }
        }
    }
    
    
    /**
     * Method to set the down button to 'off'.
     * 
     * @param elevator the elevator that has arrived for a down request
     * 
     * @throws elevatorSimulation.utility.InvalidParameterException if Person.getOnElevator() has thrown the exception
     */
    public void downArrival(Elevator elevator) throws InvalidParameterException{
        if (elevator == null){
            throw new InvalidParameterException("Elevator is null.  Cannot exchange passengers.");
        }
        setDownPressed(false);
        elevator.unloadRiders(this);
        HashSet<Person> toRemove = new HashSet();
        synchronized(getPeople()){
            for (Person p : getPeople()){
                if (p.getDestination() < getFlrNum() && elevator.getRiderCount() < elevator.getMaxCapacity()){
                    toRemove.add(p);
                }
            }
            for (Person p : toRemove){
                getPeople().remove(p);
                LogMgr.floorExitLog(getFlrNum(), p.getPersonNumber(), getPeople());
                p.getOnElevator(elevator);
            }
            for (Person p : getPeople()){
                if (p.getDestination() < getFlrNum()){
                    p.pressDown();
                    break;
                }
            }
        }
    }
    
    
    /**
     * Method to unload riders if the elevator is finished with it's trip, and load any potential riders.
     * 
     * @param elevator the elevator that has arrived on the floor
     * 
     * @throws InvalidParameterException if the method Floors.getOnElevator() has thrown the exception
     */
    public void idleUnload(Elevator elevator) throws InvalidParameterException {
        if (elevator == null){
            throw new InvalidParameterException("Elevator is null.  Cannot exchange passengers.");
        }
        elevator.unloadRiders(this);
        Person frstInLine = null;
        synchronized(getPeople()){
            for (Person p : getPeople()){
                if (p.isWaiting()){
                    frstInLine = p;
                    break;
                }
            }
            if (frstInLine == null){
                return;
            }
            if (!getPeople().isEmpty()){
                HashSet<Person> toRemove = new HashSet();
                if (frstInLine.getDestination() > getFlrNum()){
                    setUpPressed(false);
                    for (Person p : getPeople()){
                        if (p.getDestination() > getFlrNum() && elevator.getRiderCount() < elevator.getMaxCapacity()){
                            toRemove.add(p);
                        }
                    }
                    for (Person p : toRemove){
                        getPeople().remove(p);
                        LogMgr.floorExitLog(getFlrNum(), p.getPersonNumber(), getPeople());
                        p.getOnElevator(elevator);
                    }
                    for (Person p : getPeople()){
                        if (p.getDestination() < getFlrNum()){
                            p.pressUp();
                            break;
                        }
                    }
                }
                else{
                    setDownPressed(false);
                    for (Person p : getPeople()){
                        if (p.getDestination() < getFlrNum() && elevator.getRiderCount() < elevator.getMaxCapacity()){
                            toRemove.add(p);
                        }
                    }
                    for (Person p : toRemove){
                        getPeople().remove(p);
                        LogMgr.floorExitLog(getFlrNum(), p.getPersonNumber(), getPeople());
                        p.getOnElevator(elevator);
                    }
                    for (Person p : getPeople()){
                        if (p.getDestination() < getFlrNum()){
                            p.pressDown();
                            break;
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * Method to press the up button on the floor object.
     * 
     * @param personNum the number of the person pressing the button
     * @throws elevatorSimulation.utility.InvalidParameterException if Building.giveFloorCall() has thrown the exception
     */
    public void pressUp(int personNum) throws InvalidParameterException{
        if (getUpPressed() == false){
            setUpPressed(true);
            LogMgr.upPressed(getFlrNum(), personNum);
            Building.giveFloorCall(getFlrNum(), UP);
        }
    }
    
    
    /**
     * Method to press the down button on the floor object.
     * 
     * @param personNum the number of the person pressing the button
     * @throws elevatorSimulation.utility.InvalidParameterException if Building.giveFloorCall() has thrown the exception
     */
    public void pressDown(int personNum) throws InvalidParameterException{
        if (getDownPressed() == false){
            setDownPressed(true);
            LogMgr.downPressed(getFlrNum(), personNum);
            Building.giveFloorCall(getFlrNum(), DOWN);
        }
    }
    
    
    /**
     * Method to load a person onto the floor
     * 
     * @param person the person object to load on the floor
     * 
     * @throws InvalidParameterException if Person.add() has thrown the exception
     */
    public void load(Person person) throws InvalidParameterException{
        if (person == null){
            throw new InvalidParameterException("Person is null.");
        }
        synchronized(getPeople()){
            getPeople().add(person);
        }
        if (!person.isWaiting()){
            LogMgr.floorEntryLog(getFlrNum(), person.getPersonNumber(), getPeople());
        }
    }
    
    
    

    
    /**
     * Accessor to get the floor number.
     * 
     * @return the floor number
     */
    public int getFlrNum() {
        return floorNumber;
    }
    
    
    /**
     * Accessor to check if the down button is pressed or not.
     * 
     * @return true if down is pressed
     */
    private boolean getDownPressed(){
        return downPressed;
    }
    
    
    /**
     * Accessor to check if the up button is pressed or not.
     * 
     * @return true if up is pressed
     */
    private boolean getUpPressed(){
        return upPressed;
    }
    
    
    /**
     * Mutator to set the up button.
     */
    private void setUpPressed(boolean set){
        upPressed = set;
    }
    
    
    /**
     * Mutator to set the up button.
     */
    private void setDownPressed(boolean set){
        downPressed = set;
    }
    
    
    /**
     * Accessor to get a list of people currently on the floor
     * @return the people array list
     */
    private ArrayList<Person> getPeople(){
        return people;
    }
    
    
    /**
     * Mutator to set the people list for the floor.
     * 
     * @param ppl the array list of person objects to set to
     */
    private void setPeople(ArrayList<Person> ppl){
        people = ppl;
    }
}
