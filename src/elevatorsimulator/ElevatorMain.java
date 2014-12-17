package elevatorsimulator;

import elevatorSimulation.buildingObjects.Building;
import elevatorSimulation.utility.InvalidParameterException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main elevator simulation runs from this class.  Provide a csv file of the following format to begin the simulation: 
 * a string representing the elevator selector type, a string representing the pending request processor type, a string representing  
 * the elevator type, an integer representing the number of floors, an integer representing the quantity of elevators, and integer 
 * representing the time in milliseconds that the door is open when the elevator stops, an integer representing the time in milliseconds 
 * that the elevator takes to travel from one floor to another, an integer representing the default floor that the elevator begins on and 
 * travels to upon timeout, an integer representing the time in milliseconds before the elevator timeout, an integer representing the 
 * maximum capacity for an elevator, an integer representing the number of people per minute to create, an integer representing the 
 * duration in minutes for people creation.  The simulation runs appropriately and outputs information of wait times and ride times when 
 * the simulation completes.
 * 
 * @author Brandon Pauly
 */
public class ElevatorMain {


    public static void main(String[] args) throws InterruptedException {
        String csvFileToRead = "data/sim1.csv";  // 200 floors, 20 elevators, 1000ms door, 500ms speed, 100 dflt floor, 15000ms timeout, 20 max capacity, 120 people per minute, 2 minute duration
        //String csvFileToRead = "data/sim2.csv";  // 100 floors, 6 elevators, 500ms door, 500ms speed, 50 dflt floor, 15000ms timeout, 10 max capacity, 45 people per minute, 3 minute duration
        //String csvFileToRead = "data/sim3.csv";  // 16 floors, 4 elevators ,500ms door, 500ms speed, 1 dflt floor, 15000ms timeout, 8 max capacity, 15 people per minute, 5 minute duration
        BufferedReader bufR;
        String line;
        String splitter = ",";
        try{
            bufR = new BufferedReader(new FileReader(csvFileToRead));
            line = bufR.readLine();
            String[] params = line.split(splitter);
            Building building = Building.getBuildingInstance(params[0], params[1], params[2], Integer.parseInt(params[3]), 
                    Integer.parseInt(params[4]), Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7]),
                    Integer.parseInt(params[8]), Integer.parseInt(params[9]), Integer.parseInt(params[10]), Integer.parseInt(params[11]));
            bufR.close();
        }
        catch(InvalidParameterException eInv){
            System.out.println("CSV file has invalid parameters for building creation.  " + eInv.getMessage());
        }
        catch(FileNotFoundException eF){
            System.out.println("No file named " + csvFileToRead + " found.  Check filename and path.  " + eF.getMessage());
        }
        catch(IOException eIO){
            System.out.println("InputOutput error occurred.  Check input file and retry.  " + eIO.getMessage());
        }
    }
}
