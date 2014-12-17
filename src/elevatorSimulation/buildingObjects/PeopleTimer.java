package elevatorSimulation.buildingObjects;

import elevatorSimulation.utility.InvalidParameterException;

/**
 * Singleton class to act as a timer for people object creation.  Tracks duration and calls method Building.createPerson().
 * 
 * @author Brandon Pauly
 * 
 * @see elevatorSimulation.utility.InvalidParameterException
 */
public final class PeopleTimer {
    
    private volatile static PeopleTimer timerInstance;
    
    /**
     * Integer representing the number of people to create every minute.
     */
    private final int frequency;
    
    /**
     * Integer representing the duration, in minutes, for which to create people in.
     */
    private final long productionTime;
    
    /**
     * Seconds in a minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;
    
    /**
     * Milliseconds in a second.
     */
    private static final int MILLIS_PER_SECOND = 1000;
    
    
    /**
     * Get method, per the singleton design pattern, to get a reference to the single instance of people timer.
     * 
     * @param peoplePerMin integer representing the number of people to create per minute
     * @param duration integer representing the number of minutes to create people
     * 
     * @return the instance of people timer
     */
    public static PeopleTimer getTimerInstance(int peoplePerMin, int duration){
        if (getTimerInstance() == null){
            synchronized(PeopleTimer.class){
                if (getTimerInstance() == null){
                    setTimerInstance(new PeopleTimer(peoplePerMin, duration));
                }
            }
        }
        return getTimerInstance();
    }
    
    
    /**
     * Run method that starts the people timer.
     */
    public void makePeople() {
        int  millisBetPeople = SECONDS_PER_MINUTE * MILLIS_PER_SECOND / getFrequency();
        long startTime = System.currentTimeMillis();
        int personCounter = 1;
        while (System.currentTimeMillis() - startTime < getProdTime()){
            try {
                Building.createPerson(personCounter);
                personCounter++;
            } 
            catch (InvalidParameterException ex) {
                System.out.println("Wasn't able to create a person. " + ex.getMessage());
            }
            try {
                Thread.sleep(millisBetPeople);
            } 
            catch (InterruptedException ex) {
                System.out.println("Sleep interrupted for people creation.");
            }
        }
        Building.endPplProd();
    }
    
    
    /**
     * Constructor to build a people timer, which is used to manage the timing of people production.
     * 
     * @param peoplePerMin integer to represent the number of people to create per minute
     * @param duration integer to represent the number of minutes to create people
     */
    private PeopleTimer(int peoplePerMin, int duration){
        frequency = peoplePerMin;
        productionTime = duration * SECONDS_PER_MINUTE * MILLIS_PER_SECOND;
    }
    
    
    /**
     * Accessor for the production time that people should be created within.
     * 
     * @return the number of minutes that production should occur for
     */
    private long getProdTime(){
        return productionTime;
    }
    
    
    /**
     * Accessor for the frequency that people should be created at.
     * 
     * @return the number of people per minute to create
     */
    private int getFrequency(){
        return frequency;
    }
    
    
    /**
     * Accessor for the timer instance.
     * 
     * @return the single instance of timer if there is one, null otherwise
     */
    private static PeopleTimer getTimerInstance(){
        return timerInstance;
    }
    
    
    /**
     * Mutator to set the timer instance.
     * 
     * @param pT the people timer to set the timer instance to
     */
    private static void setTimerInstance(PeopleTimer pT){
        timerInstance = pT;
    }
}
