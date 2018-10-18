package rockets.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.test.rule.TestDirectory;
import rockets.dataaccess.DAO;
import rockets.dataaccess.neo4j.Neo4jDAO;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class RocketUnitTest {
    Rocket rocket;
    private static final String TEST_DB = "target/test-data/test-db";

    @ClassRule
    public static TestDirectory testDirectory = TestDirectory.testDirectory();
    private static DAO dao;
    private static Session session;
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void setUp() {
        EmbeddedDriver driver;

        driver = createImpermanentEmbeddedDriver(TEST_DB);

        sessionFactory = new SessionFactory(driver, User.class.getPackage().getName());
        session = sessionFactory.openSession();

        dao = new Neo4jDAO(sessionFactory);
    }

    private static EmbeddedDriver createImpermanentEmbeddedDriver(String fileDir) {
        GraphDatabaseService db = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder(testDirectory.keepDirectoryAfterSuccessfulTest().directory(fileDir))
                .setConfig(GraphDatabaseSettings.pagecache_memory, "64M")
                .newGraphDatabase();
        return new EmbeddedDriver(db);
    }

    @Before
    public void localSetUp() throws Exception {
        rocket = new Rocket("5", "Ariane", "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));
    }

    @Test
    public void testRocketWithSameInfoEquals(){
        Rocket rocket1 = new Rocket("5", "Ariane", "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));

        assertEquals("Same Full Name, but different rockets",rocket,rocket1);
    }

    @Test
    public void testDiffManufacturerDiffRocket(){
        Rocket rocket1 = new Rocket("5", "Ariane", "ECA", "China", new LaunchServiceProvider("RMIT", 1984, "Australia"));

        assertNotEquals("case fail:Different manufacturer, but same rocket", rocket, rocket1);
    }

    @Test
    public void testDiffNameDiffRocket(){
        Rocket rocket1 = new Rocket("4", "Ariane", "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));

        assertNotEquals("case fail:Different name, but same rocket", rocket, rocket1);
    }

    @Test
    public void testDiffFamilyNameDiffRocket(){
        Rocket rocket1 = new Rocket("5", "Apollo", "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));

        assertNotEquals("case fail:Different family name, but same rocket", rocket, rocket1);
    }

    @Test
    public void testDiffVariationNameDiffRocket(){
        Rocket rocket1 = new Rocket("5", "Ariane", "ES", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));

        assertNotEquals("case fail:Different variation name, but same rocket", rocket, rocket1);
    }

    @Test
    public void testDiffCountryDiffRocket(){
        Rocket rocket1 = new Rocket("5", "Ariane", "ECA", "America", new LaunchServiceProvider("Monash", 1984, "Australia"));

        assertNotEquals("case fail:Different country, but same rocket", rocket, rocket1);
    }

    @Test(expected = NullPointerException.class)
    public void testNullName(){
        Rocket rocket1 = new Rocket(null, "Ariane", "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));
    }

    @Test(expected = NullPointerException.class)
    public void testNullFamilyName(){
        Rocket rocket1 = new Rocket("5", null, "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));
    }

    @Test(expected = NullPointerException.class)
    public void testNullVariationName(){
        Rocket rocket1 = new Rocket("5", "Ariane", null, "China", new LaunchServiceProvider("Monash", 1984, "Australia"));
    }

    //@Test(expected = NullPointerException.class)
    //public void testNullCountry(){
      //  Rocket rocket1 = new Rocket("5", "Ariane", "ECA", null, new LaunchServiceProvider("Monash", 1984, "Australia"));
    //}

    @Test(expected = NullPointerException.class)
    public void testNullManufacturer(){
        Rocket rocket1 = new Rocket("5", "Ariane", "ECA", "China", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName(){
        Rocket rocket1 = new Rocket("", "Ariane", "ECA", "China", new LaunchServiceProvider("ArianeGroup", 1984, "Australia"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyFamilyName(){
        Rocket rocket1 = new Rocket("5", "", "ECA", "China", new LaunchServiceProvider("ArianeGroup", 1984, "Australia"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyVariationName(){
        Rocket rocket1 = new Rocket("5", "Ariane", "", "China", new LaunchServiceProvider("ArianeGroup", 1984, "Australia"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCountry(){
        Rocket rocket1 = new Rocket("5", "Ariane", "ECA", "", new LaunchServiceProvider("ArianeGroup", 1984, "Australia"));
    }

    @Test (expected = NullPointerException.class)
    public void testNullForCountry(){
        rocket.setCountry(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyForCountry(){
        rocket.setCountry("");
    }

    @Test (expected = NullPointerException.class)
    public void testNullMassLEO(){
        rocket.setMassToLEO(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullMassGTO(){
        rocket.setMassToGTO(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyMassLEO(){
        rocket.setMassToLEO("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyMassGTO(){
        rocket.setMassToGTO("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoOfLaunchGreaterThanZero(){
        rocket.setNoLaunches(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLaunchNoGreaterThan1IfFirstYearFlightNotZero(){
        rocket.setFirstYearFlight(1990);
        rocket.setNoLaunches(0);
    }

    @Test
    public void testSetNoOfLaunchWhenOnlyLaunchOnce (){
        rocket.setFirstYearFlight(1990);
        rocket.setNoLaunches(1);
        assertEquals("Case fail: the rocket only launch one time, but fail to set No. of launch as 1",1,rocket.getNoLaunches());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetNoOfLaunchToZeroIfHasFirstLauch(){
        rocket.setFirstYearFlight(1990);
        rocket.setNoLaunches(0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testLatestYearGreaterThanFirstYearNoOfLaunch(){
        rocket.setFirstYearFlight(1990);
        rocket.setLatestYearFlight(1991);
        rocket.setNoLaunches(1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testLatestYearEqaualFirstYearNoOfLaunch(){
        rocket.setFirstYearFlight(1990);
        rocket.setLatestYearFlight(1990);
        rocket.setNoLaunches(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public  void testFirstYearNotValid(){
        rocket.setFirstYearFlight(1878);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFirstYearLaterThanLastYear(){
        rocket.setFirstYearFlight(1980);
        rocket.setLatestYearFlight(1960);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestYearNotValid(){
        rocket.setLatestYearFlight(1780);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestYearEarlyThanFirstYear(){
        rocket.setFirstYearFlight(1990);
        rocket.setLatestYearFlight(1989);
    }

    @Test
    public void testFirstYearEqualLatestYear(){
        rocket.setFirstYearFlight(1990);
        rocket.setLatestYearFlight(1990);

        assertEquals("Case fail:fail to set first and latest launch year same value.", rocket.getFirstYearFlight(),rocket.getLatestYearFlight());
    }

    @Test
    public void testDiffNameDiffHashcode(){
        Rocket roc = new Rocket("4", "Ariane", "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));
        assertNotEquals("Case Fail:different name,but same hash code.", rocket.hashCode(), roc.hashCode());
    }

    @Test
    public void testDiffFamilyNameDiffHashcode(){
        Rocket roc = new Rocket("5", "Apllo", "ECA", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));
        assertNotEquals("Case Fail:different family name,but same hash code.", rocket.hashCode(), roc.hashCode());
    }

    @Test
    public void testDiffVariationNameDiffHashcode(){
        Rocket roc = new Rocket("5", "Ariane", "ET", "China", new LaunchServiceProvider("Monash", 1984, "Australia"));
        assertNotEquals("Case Fail:different variation name,but same hash code.", rocket.hashCode(), roc.hashCode());
    }

    @Test
    public void testDiffCountryDiffHashcode(){
        Rocket roc = new Rocket("5", "Ariane", "ECA", "EU", new LaunchServiceProvider("Monash", 1984, "Australia"));
        assertNotEquals("Case Fail:different country,but same hash code.", rocket.hashCode(), roc.hashCode());
    }

    @Test
    public void testDiffManufacturerDiffHashcode(){
        Rocket roc = new Rocket("5", "Ariane", "ECA", "China", new LaunchServiceProvider("DLA", 1984, "Australia"));
        assertNotEquals("Case Fail:different manufacturer,but same hash code.", rocket.hashCode(), roc.hashCode());
    }

    @Test
    public void testSuccSetLatestYear(){
        rocket.setFirstYearFlight(1980);
        rocket.setLatestYearFlight(1990);
        assertEquals(String.format("Case Fail:first year is {0},but fail to set latest year {1}",
                1980,1990), 1990,
                rocket.getLatestYearFlight());
    }

    @Test
    public void testSuccSetFirstYear(){
        rocket.setFirstYearFlight(1990);
        rocket.setLatestYearFlight(1991);

        assertEquals(String.format("Case Fail:latest year is {0},but fail to set latest year {1}",
                1991,1990), 1990,
                rocket.getFirstYearFlight());
    }

    @Test
    public void testSuccSetNoOfLaunch(){
        rocket.setFirstYearFlight(1990);
        rocket.setLatestYearFlight(1991);
        rocket.setNoLaunches(2);

        assertEquals(String.format("Case Fail:latest year is {0},first year {1}, fail to set No. of launch as 2",
                1991,1990), 2,
                rocket.getNoLaunches());
    }

    @Test
    public void testSuccSetMassToOthers(){
        rocket.setMassToOther("3,600 to SSO");
        assertEquals("3,600 to SSO", rocket.getMassToOther());
    }

    @Test
    public void testSuccSetsetMassToGTO(){
        rocket.setMassToGTO("12,000");
        assertEquals("12,000", rocket.getMassToGTO());
    }

    @Test
    public void testSuccSetsetMassToLEO(){
        rocket.setMassToLEO("12,000");
        assertEquals("12,000", rocket.getMassToLEO());
    }


   /* @Test
    public void testValidManufacturer () {
        LaunchServiceProvider fakeLsp = new LaunchServiceProvider("fakeLSP", 2001, "Australia");
        rocket = new Rocket("Chang E", "ShenZhou", "V", "China", fakeLsp);

        try {
            dao.createOrUpdate(rocket);
            fail("Fail: manufacturer not exist, but rocket created");
        } catch (IllegalArgumentException ex) {
            assertTrue("Throws IAE", ex instanceof IllegalArgumentException);
            assertTrue("launch service provider not exist..", ex.getMessage().contains("launch service provider"));
            assertNull("rocket saved", rocket.getId());
            assertTrue("fail:should be no rocket, but there is one.", dao.loadAll(Rocket.class).size() == 0);
            System.out.println("testValidManufacturer case pass.");
        }
    }*/
}