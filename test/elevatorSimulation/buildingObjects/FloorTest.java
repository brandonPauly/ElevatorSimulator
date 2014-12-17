package elevatorSimulation.buildingObjects;

import static elevatorSimulation.buildingObjects.Building.getBuildingInstance;
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
public class FloorTest {
    
    public FloorTest() {
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
     * Test of upArrival method, of class Floor.
     */
    @Test
    public void testUpArrival() throws InvalidParameterException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        System.out.println("upArrival");
        Building b = getBuildingInstance("Standard", "Standard", "Standard", 8, 1, 1, 1, 1, 1000, 5, 1, 1);
        Floor instance = new Floor(5);
        instance.pressUp(3);
        instance.upArrival(new StandardElevator(1, 500, 500, 8, 1, 15000, 5));
        Field field = Floor.class.getDeclaredField("upPressed");
        field.setAccessible(true);
        assertFalse(field.getBoolean(instance));
    }

    /**
     * Test of downArrival method, of class Floor.
     */
    @Test
    public void testDownArrival() throws InvalidParameterException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        System.out.println("downArrival");
        Building b = getBuildingInstance("Standard", "Standard", "Standard", 8, 1, 1, 1, 1, 1000, 5, 1, 1);
        Floor instance = new Floor(3);
        instance.pressDown(2);
        instance.downArrival(new StandardElevator(2, 500, 500, 8, 1, 15000, 5));
        Field field = Floor.class.getDeclaredField("downPressed");
        field.setAccessible(true);
        assertFalse(field.getBoolean(instance));
    }
    

    /**
     * Test of pressUp method, of class Floor.
     */
    @Test
    public void testPressUp() throws InvalidParameterException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        System.out.println("pressUp");
        Building b = getBuildingInstance("Standard", "Standard", "Standard", 8, 1, 1, 1, 1, 1000, 5, 1, 1);
        Floor instance = new Floor(5);
        instance.pressUp(1);
        Field field = Floor.class.getDeclaredField("upPressed");
        field.setAccessible(true);
        assertTrue(field.getBoolean(instance));
    }

    /**
     * Test of pressDown method, of class Floor.
     */
    @Test
    public void testPressDown() throws InvalidParameterException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        System.out.println("pressDown");
        Building b = getBuildingInstance("Standard", "Standard", "Standard", 8, 1, 1, 1, 1, 1000, 5, 1, 1);
        Floor instance = new Floor(5);
        instance.pressDown(2);
        Field field = Floor.class.getDeclaredField("downPressed");
        field.setAccessible(true);
        assertTrue(field.getBoolean(instance));    
    }
}
