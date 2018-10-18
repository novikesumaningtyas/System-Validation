package rockets.model;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class LaunchServiceProviderUnitTest {
    LaunchServiceProvider lsProvider; //declare default test object

    @Before
    public void setUp() throws Exception {

        lsProvider = new LaunchServiceProvider("CCP", 1990, "China");
    }

    @Test(expected = NullPointerException.class)
    public void testNameNull(){
        lsProvider = new LaunchServiceProvider(null, 1990, "China");
    }

   // @Test(expected = NullPointerException.class)
    //public void testCountryNull(){
        //lsProvider = new LaunchServiceProvider("XD", 1990, null);
    //}

    /*@Test
    public void testIllegalYear(){
        try{
            lsProvider = new LaunchServiceProvider("XD", 1758, "Russia");
            fail("Year is illegal.Should > 1957");
        } catch (Exception e){
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Year is illegal.Should > 1957", e.getMessage().contains("Year"));
            System.out.println("testIllegalYear case pass.");
        }
    }*/

    @Test
    public void testNullEmptyHeadquaters(){
        LaunchServiceProvider provider1 = new LaunchServiceProvider("Provider1", 1990, "China");

        try {
            provider1.setHeadquarters(null);
            fail("Headquarters cannot be null or empty.");
        } catch(Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Headquarters cannot be null or empty.", e.getMessage().contains("Headquarters"));
            System.out.println("testNullEmptyHeadquaters case pass.");
        }

        try {
            provider1.setHeadquarters("");
            fail("Headquarters cannot be null or empty.");
        } catch(Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Headquarters cannot be null or empty.", e.getMessage().contains("Headquarters"));
            System.out.println("testNullEmptyHeadquaters case pass.");
        }
    }

    @Test
    public void countryNotNullOrEmpty(){
        LaunchServiceProvider provider1 = new LaunchServiceProvider("Provider1", 1990, "");

        try{
            provider1.setCountry(null);
            fail("Country cannot be null");
        } catch (Exception e)
        {
            assertTrue("Throws Illegal Argument Exception", e instanceof IllegalArgumentException);
            assertTrue("Message contains null", e.getMessage().contains("null"));
        }

        try{
            provider1.setCountry("");
            fail("Country cannot be empty");
        } catch(Exception e)
        {
            assertTrue("Throws Illegal Argument Exception", e instanceof IllegalArgumentException);
            assertTrue("Message contains empty", e.getMessage().contains("empty"));
        }
    }

    //@Test(expected = NullPointerException.class)
    //public void testNullRocketSet(){
        //lsProvider.setRockets(null);
    //}

    /*@Test
    public void testRocketSetRemoveNullElement(){
        Set<Rocket> set = Sets.newLinkedHashSet();
        set.add(null);
        set.add(new Rocket("5", "Ariane","ECT","China",new LaunchServiceProvider("CCP", 1984, "Australia")));
        set.add(new Rocket("5", "Ariane","VC","South Africa", new LaunchServiceProvider("Kangroo", 1984, "Australia")));

        lsProvider.setRockets(set);
        assertFalse(lsProvider.getRockets().contains(null));
    }*/

    @Test
    public void testSetRocketWithNull() {
        try {
            lsProvider.setRockets(null);
            fail("Rockets cannot be null");
        } catch (Exception e) {
            assertTrue("Throws Illegal Argument Exception", e instanceof IllegalArgumentException);
            assertTrue("Payload is null", e.getMessage().contains("null"));
        }
    }

    @Test
    public void testDifferentYearDiffProvider(){
        LaunchServiceProvider p2 = new LaunchServiceProvider("CCP", 1991, "China");
        assertFalse(lsProvider.equals(p2));
    }

    @Test
    public void testDifferentNameDiffProvider(){
        LaunchServiceProvider p2 = new LaunchServiceProvider("XI", 1990, "China");
        assertFalse(lsProvider.equals(p2));
    }

    @Test
    public void testDifferentCountryDiffProvider(){
        LaunchServiceProvider p2 = new LaunchServiceProvider("CCP", 1990, "Russia");
        assertFalse(lsProvider.equals(p2));
    }

    @Test
    public void testNullDiffProvider(){
        LaunchServiceProvider p2 = null;
        assertFalse(lsProvider.equals(p2));
    }

    @Test
    public void testSameProviderSameHashCode(){
        LaunchServiceProvider p2 = lsProvider;
        assertEquals("Same Rocket", lsProvider, p2);
    }

    @Test
    public void testDiffProviderDiffHashCode(){
        LaunchServiceProvider p2 = new LaunchServiceProvider("IU", 1989, "German");
        assertNotEquals("Different Rocket", lsProvider, p2);
    }
}