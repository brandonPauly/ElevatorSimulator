package elevatorSimulation.elevatorObjects;

import elevatorSimulation.buildingObjects.Building;
import static elevatorSimulation.buildingObjects.Building.getBuildingInstance;
import elevatorSimulation.buildingObjects.Floor;
import elevatorSimulation.buildingObjects.Person;
import elevatorSimulation.utility.InvalidParameterException;
import java.lang.reflect.Field;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Brandon Pauly
 */
public class ElevatorImplTest {
    
    public ElevatorImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

     /**
     * Test of getElevNum method, of class ElevatorImpl.
     */
    @Test
    public void testGetElevNum() {
        System.out.println("getElevNum");
        ElevatorImpl instance = new ElevatorImpl(1, 500, 500, 8, 1, 15000, 5);
        Thread t = new Thread(instance);
        t.start();
        int expResult = 1;
        int result = instance.getElevNum();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentFloor method, of class ElevatorImpl.
     */
    @Test
    public void testGetCurrentFloor() {
        System.out.println("getCurrentFloor");
        ElevatorImpl instance = new ElevatorImpl(1, 500, 500, 8, 1, 15000, 5);
        Thread t = new Thread(instance);
        t.start();
        int expResult = 1;
        int result = instance.getCurrentFloor();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRiderCount method, of class ElevatorImpl.
     */
    @Test
    public void testGetRiderCount() {
        System.out.println("getRiderCount");
        ElevatorImpl instance = new ElevatorImpl(1, 500, 500, 8, 1, 15000, 5);
        Thread t = new Thread(instance);
        t.start();
        int expResult = 0;
        int result = instance.getRiderCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDirection method, of class ElevatorImpl.
     */
    @Test
    public void testGetDirection() {
        System.out.println("getDirection");
        ElevatorImpl instance = new ElevatorImpl(1, 500, 500, 8, 1, 15000, 5);
        Thread t = new Thread(instance);
        t.start();
        int result = instance.getDirection();
        assertEquals(0, result);
    }

/**
     * Test of run method, of class ElevatorImpl.
     */
    @Test
    public void testRun() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        System.out.println("run");
        ElevatorImpl instance = new ElevatorImpl(1, 500, 500, 8, 1, 15000, 5);
        Thread t = new Thread(instance);
        t.start();
        Field field = ElevatorImpl.class.getDeclaredField("running");
        field.setAccessible(true);
        assertTrue(field.getBoolean(instance));
    }
   


    /**
     * Test of stop method, of class ElevatorImpl.
     */
    @Test
    public void testStop() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        System.out.println("stop");
        ElevatorImpl instance = new ElevatorImpl(1, 500, 500, 8, 1, 15000, 5);
        Thread t = new Thread(instance);
        t.start();
        instance.stop();
        Field field = ElevatorImpl.class.getDeclaredField("running");
        field.setAccessible(true);
        assertFalse(field.getBoolean(instance));
    }
    
    @Test
    public void testLoad() throws InvalidParameterException{
        System.out.println("load");
        Building b = getBuildingInstance("Standard", "Standard", "Standard", 2, 1, 1, 1, 1, 1, 5, 1, 1);
        ElevatorImpl instance = new ElevatorImpl(1, 500, 500, 8, 1, 15000, 5);
        Person person = new Person(1, new Floor(1), 4);
        instance.load(person);
        assertEquals(1, instance.getRiderCount());
    }
}
