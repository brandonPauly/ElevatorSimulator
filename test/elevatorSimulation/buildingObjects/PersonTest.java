package elevatorSimulation.buildingObjects;

import elevatorSimulation.elevatorObjects.Elevator;
import elevatorSimulation.elevatorObjects.StandardElevator;
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
public class PersonTest {
    
    public PersonTest() {
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
     * Test of getOnElevator method, of class Person.
     */
    @Test
    public void testGetOnElevator() throws Exception {
        System.out.println("getOnElevator");
        Building b = Building.getBuildingInstance("Standard", "Standard", "Standard", 2, 1, 500, 500, 1, 15000, 2, 1, 1);
        Elevator elevator = new StandardElevator(1, 500, 500, 8, 1, 15000, 5);
        Person instance = new Person(1, new Floor(5), 2);
        instance.getOnElevator(elevator);
        assertFalse(instance.isWaiting());
    }

    /**
     * Test of getOffElevator method, of class Person.
     */
    @Test
    public void testGetOffElevator() throws Exception {
        System.out.println("getOffElevator");
        Building b = Building.getBuildingInstance("Standard", "Standard", "Standard", 2, 1, 500, 500, 1, 15000, 2, 1, 1);
        Floor floor = new Floor(5);
        Person instance = new Person(1, new Floor(3), 2);
        Elevator elevator = new StandardElevator(1, 500, 500, 8, 1, 15000, 2);
        elevator.load(instance);
        instance.getOnElevator(elevator);
        instance.getOffElevator(floor);
        Field field = Person.class.getDeclaredField("currentElevator");
        field.setAccessible(true);
        assertNull(field.get(instance));
    }

    /**
     * Test of isWaiting method, of class Person.
     */
    @Test
    public void testIsWaiting() throws InvalidParameterException {
        System.out.println("isWaiting");
        Person instance = new Person(1, new Floor(2), 3);
        boolean expResult = true;
        boolean result = instance.isWaiting();
        assertEquals(expResult, result);
        instance.getOnElevator(new StandardElevator(1, 500, 500, 8, 1, 15000, 5));
        assertFalse(instance.isWaiting());
    }

    /**
     * Test of getSrcFlr method, of class Person.
     */
    @Test
    public void testGetSrcFlr() throws InvalidParameterException {
        System.out.println("getSrcFlr");
        Person instance = new Person(1, new Floor(2), 2);
        int expResult = 2;
        int result = instance.getSrcFlr();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDestination method, of class Person.
     */
    @Test
    public void testGetDestination() throws InvalidParameterException {
        System.out.println("getDestination");
        Person instance = new Person(1, new Floor(5), 6);
        int expResult = 6;
        int result = instance.getDestination();
        assertEquals(expResult, result);
    }

    /**
     * Test of pressUp method, of class Person.
     */
    @Test
    public void testPressUp() throws Exception {
        System.out.println("pressUp");
        Floor floor = new Floor(2);
        Person instance = new Person(1, floor, 5);
        instance.pressUp();
        Field field = Floor.class.getDeclaredField("upPressed");
        field.setAccessible(true);
        assertTrue(field.getBoolean(floor));
    }

    /**
     * Test of pressDown method, of class Person.
     */
    @Test
    public void testPressDown() throws Exception {
        System.out.println("pressDown");
        Floor floor = new Floor(2);
        Person instance = new Person(1, floor, 5);
        instance.pressDown();
        Field field = Floor.class.getDeclaredField("downPressed");
        field.setAccessible(true);
        assertTrue(field.getBoolean(floor));
    }

    /**
     * Test of getPersonNumber method, of class Person.
     */
    @Test
    public void testGetPersonNumber() throws InvalidParameterException {
        System.out.println("getPersonNumber");
        Person instance = new Person(1, new Floor(3), 3);
        int expResult = 1;
        int result = instance.getPersonNumber();
        assertEquals(expResult, result);
    }
    
}
