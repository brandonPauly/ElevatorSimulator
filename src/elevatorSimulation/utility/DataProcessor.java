package elevatorSimulation.utility;

import elevatorSimulation.buildingObjects.Person;
import java.util.ArrayList;

/**
 * This class is the data processor for the wait and ride time info on all people.  When the simulation ends,
 * the array list of people that existed in the building is passed to this class.  The data processor sifts through the
 * people and calculates, arranges, and outputs the data in tables.
 * 
 * @author Brandon Pauly
 */
public final class DataProcessor {
    /**
     * A constant representing the number of milliseconds in a second.
     */
    private static final long MILLIS_IN_SECOND = 1000;
    
    /**
     * A dummy value representing nil, or in this context N/A.
     */
    private static final long N_A = -1;
    
    /**
     * A constant representing the largest value that can be stored as a long.
     */
    private static final long INFINITY = Long.MAX_VALUE;
    
    /**
     * The array of average wait times.
     */
    private static long[] avgWait;
    
    /**
     * The array of minimum wait times.
     */
    private static long[] minWait;
    
    /**
     * The array of maximum wait times.
     */
    private static long[] maxWait;
    
    /**
     * The two dimensional array of average ride times from one floor to another.
     */
    private static long[][] avgRide;
    
    /**
     * The two dimensional array of minimum ride times from one floor to another.
     */
    private static long[][] minRide;
    
    /**
     * The two dimensional array of maximum ride times from one floor to another.
     */
    private static long[][] maxRide;
    
    /**
     * The array list of people in the building.
     */
    private static ArrayList<Person> people;
    
    /**
     * Number of floors in the building.
     */
    private static int floors;
    
    
    /**
     * This method begins the data processing for the tables of information on person ride and wait times.
     * This initializes global variables and calls the functions to perform the data processing.
     * 
     * @param numFlrs the number of floors in the building
     * @param ppl the array list of people that existed in the building
     */
    public static void processPersonData(int numFlrs, ArrayList<Person> ppl){
        setPeople(ppl);
        setFloors(numFlrs);
        initArrays(numFlrs);
        calcWts();
        calcRides();
        wtTableOut();
        avgRdTableOut();
        maxRdTableOut();
        minRdTableOut();
        personTableOut();
    }
    
    
    /**
     * Constructs the person table from each person's wait, ride, and floor information and outputs it in an appropriate format.
     */
    private static void personTableOut(){
        System.out.println("\n\n\n");
        System.out.println("\t======================================== Person Table ===========================================");
        System.out.println("\t=================================================================================================");
        System.out.println("\t|   Person   | Start Floor | Destination Floor |   Wait Time   |   Ride Time   |   Total Time   |");
        System.out.println("\t=================================================================================================");
        for (Person p : getPeople()){
            int pNum = p.getPersonNumber();
            System.out.format("\t| Person %3d |  Floor %3d  |     Floor %3d     |  %3d seconds  |  %3d seconds  |  %3d seconds   |", pNum, p.getSrcFlr(),
                    p.getDestination(), p.getWaitTime()/MILLIS_IN_SECOND + (p.getWaitTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0), 
                    p.getRideTime()/MILLIS_IN_SECOND + (p.getRideTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0), 
                    (p.getRideTime()/MILLIS_IN_SECOND + (p.getRideTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0)) + 
                            (p.getWaitTime()/MILLIS_IN_SECOND + (p.getWaitTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0)));
            System.out.println("\n\t-------------------------------------------------------------------------------------------------");
        }
        System.out.println("\t=================================================================================================");
    }
    
    
    /**
     * Constructs the maximum ride table and outputs it in an appropriate format.
     */
    private static void maxRdTableOut(){
        System.out.println("\n\n\n");
        System.out.println("Maximum ride times floor by floor:\n");
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
        System.out.println();
        System.out.print("\t| Floor |");
        for (int f = 0; f < getFloors(); f++){
            System.out.format(" %3d   |", f+1);
        }
        System.out.println();
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
        System.out.println();
        for (int f1 = 0; f1 < getFloors(); f1++){
            System.out.format("\t|  %3d ||", f1+1);
            for (int f2 = 0; f2 < getFloors(); f2++){
                System.out.format("  %3s  |", (getMaxRide()[f1][f2] == N_A ? "N/A" : getMaxRide()[f1][f2]));
            }
            System.out.println();
            System.out.print("\t---------");
            for (int f3 = 0; f3 < getFloors(); f3++){
                System.out.print("--------");
            }
            System.out.println();
        }
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
    }
    
    
    /**
     * Constructs the minimum ride table and outputs it in an appropriate format.
     */
    private static void minRdTableOut(){
        System.out.println("\n\n\n");
        System.out.println("Minimum ride times floor by floor:\n");
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
        System.out.println();
        System.out.print("\t| Floor |");
        for (int f = 0; f < getFloors(); f++){
            System.out.format(" %3d   |", f+1);
        }
        System.out.println();
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
        System.out.println();
        for (int f1 = 0; f1 < getFloors(); f1++){
            System.out.format("\t|  %3d ||", f1+1);
            for (int f2 = 0; f2 < getFloors(); f2++){
                System.out.format("  %3s  |", (getMinRide()[f1][f2] == N_A ? "N/A" : getMinRide()[f1][f2]));
            }
            System.out.println();
            System.out.print("\t---------");
            for (int f3 = 0; f3 < getFloors(); f3++){
                System.out.print("--------");
            }
            System.out.println();
        }
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
    }
    
    
    /**
     * Constructs the wait table and outputs it in an appropriate format.
     */
    private static void wtTableOut(){
        System.out.println("\n\n\n");
        System.out.println("\t================================ Wait Table =================================");
        System.out.println("\t=============================================================================");
        System.out.println("\t|   Floor   |   Average Wait Time   |   Min Wait Time   |   Max Wait Time   |");
        System.out.println("\t=============================================================================");
        for (int f = 0; f < getFloors(); f++){
            System.out.format("\t| Floor %3d |     %12s      |   %12s    |   %12s    |", 
                    f+1, (getAvgWait()[f] == N_A ? "N/A     " : getAvgWait()[f] + " seconds"), 
                    (getMinWait()[f] == N_A ? "N/A     " : getMinWait()[f] + " seconds"),
                    (getMaxWait()[f] == N_A ? "N/A     " : getMaxWait()[f] + " seconds"));
            System.out.println("\n\t-----------------------------------------------------------------------------");
        }
        System.out.println("\t=============================================================================");
    }
    
    
    /**
     * Constructs the average ride table and outputs it in an appropriate format.
     */
    private static void avgRdTableOut(){
        System.out.println("\n\n\n");
        System.out.println("Average ride times floor by floor:\n");
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
        System.out.println();
        System.out.print("\t| Floor |");
        for (int f = 0; f < getFloors(); f++){
            System.out.format(" %3d   |", f+1);
        }
        System.out.println();
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
        System.out.println();
        for (int f1 = 0; f1 < getFloors(); f1++){
            System.out.format("\t|  %3d ||", f1+1);
            for (int f2 = 0; f2 < getFloors(); f2++){
                System.out.format("  %3s  |", (getAvgRide()[f1][f2] == N_A ? "N/A" : getAvgRide()[f1][f2]));
            }
            System.out.println();
            System.out.print("\t---------");
            for (int f3 = 0; f3 < getFloors(); f3++){
                System.out.print("--------");
            }
            System.out.println();
        }
        System.out.print("\t=========");
        for (int f = 0; f < getFloors(); f++){
            System.out.print("========");
        }
    }
    
    
    /**
     * Initializes all arrays for calculation and data processing.
     * 
     * @param numFlrs the number of floors in the building
     */
    private static void initArrays(int numFlrs){
        avgWait = new long[numFlrs];
        minWait = new long[numFlrs];
        maxWait = new long[numFlrs];
        avgRide = new long[numFlrs][numFlrs];
        minRide = new long[numFlrs][numFlrs];
        maxRide = new long[numFlrs][numFlrs];
        for (int f1 = 0; f1 < numFlrs; f1++){
            getAvgWait()[f1] = N_A;
            getMinWait()[f1] = N_A;
            getMaxWait()[f1] = N_A;
            for (int f2 = 0; f2 < numFlrs; f2++){
                getAvgRide()[f1][f2] = N_A;
                getMaxRide()[f1][f2] = N_A;
                getMinRide()[f1][f2] = N_A;
            }
        }
    }
    
    
    /**
     * Calculates the wait times for each person and places the appropriate values into the minimum wait array, the maximum 
     * wait array, and the average wait array.
     */
    private static void calcWts(){
        for (int f = 0; f < getFloors(); f++){
            long wtSum = 0;
            long ppf = 0;
            long minWt = INFINITY;
            long maxWt = N_A;
            for (Person p : getPeople()){
                if (p.getSrcFlr() == f+1){
                    wtSum += p.getWaitTime();
                    ppf++;
                    if (p.getWaitTime()/MILLIS_IN_SECOND + (p.getWaitTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0) < minWt){
                        minWt = p.getWaitTime()/MILLIS_IN_SECOND + (p.getWaitTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0);
                    }
                    if (p.getWaitTime()/MILLIS_IN_SECOND + (p.getWaitTime()%MILLIS_IN_SECOND > 500 ? 1 : 0) > maxWt){
                        maxWt = p.getWaitTime()/MILLIS_IN_SECOND + (p.getWaitTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0);
                    }
                }
            }
            if (ppf != 0){
                getAvgWait()[f] = wtSum/ppf/MILLIS_IN_SECOND + (wtSum/ppf%MILLIS_IN_SECOND >= 500 ? 1 : 0);
            }
            if (minWt != INFINITY){
                getMinWait()[f] = minWt;
            }
            if (maxWt != N_A){
                getMaxWait()[f] = maxWt;
            }
        }
    }
    
    
    /**
     * Calculates the ride times for each person and places the appropriate values in the minimum ride array, the maximum ride array, and 
     * the average ride array.
     */
    private static void calcRides(){
        for (int f1 = 0; f1 < getFloors(); f1++){
            for (int f2 = 0; f2 < getFloors(); f2++){
                long rdSum = 0;
                long ppf = 0;
                long minRd = INFINITY;
                long maxRd = N_A;
                for (Person p : getPeople()){
                    if ((p.getSrcFlr() == f1 + 1 && p.getDestination() == f2 + 1) || (p.getSrcFlr() == f2 + 1 && p.getDestination() == f1 + 1)){
                        rdSum += p.getRideTime();
                        ppf++;
                        if (p.getRideTime()/MILLIS_IN_SECOND + (p.getRideTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0) < minRd){
                            minRd = p.getRideTime()/MILLIS_IN_SECOND + (p.getRideTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0);
                        }
                        if (p.getRideTime()/MILLIS_IN_SECOND + (p.getRideTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0) > maxRd){
                            maxRd = p.getRideTime()/MILLIS_IN_SECOND + (p.getRideTime()%MILLIS_IN_SECOND >= 500 ? 1 : 0);
                        }
                    }
                }
                if (ppf != 0){
                    getAvgRide()[f1][f2] = rdSum/ppf/MILLIS_IN_SECOND + (rdSum/ppf%MILLIS_IN_SECOND >= 500 ? 1 : 0);
                }
                if (minRd != INFINITY){
                    getMinRide()[f1][f2] = minRd;
                }
                if (maxRd != N_A){
                    getMaxRide()[f1][f2] = maxRd;
                }
            }
        }
    }
    
    
    /**
     * Accessor for the average wait array.
     * 
     * @return the array with all of the average wait times by floor
     */
    private static long[] getAvgWait(){
        return avgWait;
    }
    
    
    /**
     * Accessor for the minimum wait array.
     * 
     * @return the array with all of the minimum wait times by floor
     */
    private static long[] getMinWait(){
        return minWait;
    }
    
    
    /**
     * Accessor for the maximum wait array.
     * 
     * @return the array with all of the maximum wait times by floor
     */
    private static long[] getMaxWait(){
        return maxWait;
    }
    
    
    /**
     * Accessor for the average ride two dimensional array.
     * 
     * @return the two dimensional array with average ride times from one floor to another
     */
    private static long[][] getAvgRide(){
        return avgRide;
    }
    
    
    /**
     * Accessor for the maximum ride two dimensional array.
     * 
     * @return the two dimensional array with the maximum ride times from one floor to another
     */
    private static long[][] getMaxRide(){
        return maxRide;
    }
    
    
    /**
     * Accessor for the minimum ride two dimensional array.
     * 
     * @return the two dimensional array with the minimum ride times from one floor to another
     */
    private static long[][] getMinRide(){
        return minRide;
    }
    
    
    /**
     * Accessor for the number of floors there were in the building.
     * 
     * @return the number of floors that were in the building
     */
    private static int getFloors(){
        return floors;
    }
    
    
    /**
     * Accessor for the list of people that existed in the building.
     * 
     * @return the array list of people
     */
    private static ArrayList<Person> getPeople(){
        return people;
    }
    
    
    /**
     * Mutator to set the list of people.
     * 
     * @param ppl the array list of people to set to
     */
    private static void setPeople(ArrayList<Person> ppl){
        people = ppl;
    }
    
    
    /**
     * Mutator to set the number of floors in the building.
     * 
     * @param flrs the number of floors to set to
     */
    private static void setFloors(int flrs){
        floors = flrs;
    }
    
    
    /**
     * Private constructor to ensure the static class never creates an instance.
     */
    private DataProcessor(){}
}
