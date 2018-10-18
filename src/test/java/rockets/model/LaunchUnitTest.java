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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class LaunchUnitTest {
    Launch launch = null;
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
    public void localSetUp(){
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU", new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        launch = new Launch(new Date(2000, 1, 23), rocket, provider,"London","LEO");
    }

    @Test(expected = NullPointerException.class)
    public void testNullDate(){
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        launch = new Launch(null, rocket, provider,"London","LEO");
    }

    @Test(expected = NullPointerException.class)
    public void testNullRocket(){
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        launch = new Launch(new Date(2000, 1, 23), null, provider,"London","LEO");
    }

    @Test(expected = NullPointerException.class)
    public void testNullPovider(){
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        launch = new Launch(new Date(2000, 1, 23), rocket, null,"London","LEO");
    }

    @Test(expected = NullPointerException.class)
    public void testNullLaunchSite(){
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        launch = new Launch(new Date(2000, 1, 23), rocket, provider,null,"LEO");
    }

    @Test(expected = NullPointerException.class)
    public void testNullOrbit(){
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        launch = new Launch(new Date(2000, 1, 23), rocket, provider,"France",null);
    }

    @Test
    public void testEmptyValueSetLaunchSite(){
        try {
            launch.setLaunchSite("");
            fail("Launch site cannot be empty.");
        } catch(Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Launch site cannot be empty.", e.getMessage().contains("site"));
            System.out.println("testEmptyValueSetLaunchSite case pass.");
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullFunction(){
        launch.setFunction(null);
    }

    @Test
    public void testEmptyFunction(){
        try {
            launch.setFunction("");
            fail("function cannot be empty.");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("function cannot be empty.", e.getMessage().contains("function"));
            System.out.println("testEmptyFunction case pass.");
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullOutcome(){
        launch.setLaunchOutcome(null);
    }

    @Test
    public void testSuccessSetOutcome(){
        launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
        assertTrue(launch.getLaunchOutcome().equals(Launch.LaunchOutcome.SUCCESSFUL));
    }

    @Test
    public void testDiffDateDiffLaunch() {
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        Launch l2  = new Launch(new Date(2010, 1, 23), rocket, provider,"London","LEO");

        assertNotEquals(launch ,l2);
    }

    @Test
    public void testDiffRocketDiffLaunch() {
        Rocket rocket = new Rocket("4", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        Launch l2  = new Launch(new Date(2000, 1, 23), rocket, provider,"London","LEO");

        assertNotEquals(launch ,l2);
    }

    @Test
    public void testDiffProviderDiffLaunch() {
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1990, "France");
        Launch l2  = new Launch(new Date(2000, 1, 23), rocket, provider,"London","LEO");

        assertNotEquals(launch ,l2);
    }

    @Test
    public void testDiffOrbitDiffLaunch() {
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        Launch l2  = new Launch(new Date(2000, 1, 23), rocket, provider,"London","GTO");

        assertNotEquals(launch ,l2);
    }

    @Test
    public void testSameLaunch() {
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        Launch l2 = new Launch(new Date(2000, 1, 23), rocket, provider,"London","LEO");

        assertEquals(launch ,l2);
    }

    @Test
    public void testSameLauchSamefHashcode() {
        Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
        LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
        Launch l2 = new Launch(new Date(2000, 1, 23), rocket, provider,"London","LEO");

        assertEquals(launch.hashCode(), l2.hashCode());
    }

    @Test
    public void testSetSuccessSite(){
        launch.setLaunchSite("China");
        assertEquals("China", launch.getLaunchSite());
    }

    @Test
    public void testSetSuccessFunction(){
        launch.setFunction("Militry");
        assertEquals("Militry", launch.getFunction());
    }

    @Test
    public void testLaunchTimeEarlierThanRocketFirstLauchYear(){
        try {
            Rocket rocket = new Rocket("5", "Ariane","ECT", "EU",new LaunchServiceProvider("FEO Technology", 1984, "Australia"));
            rocket.setFirstYearFlight(2010);
            LaunchServiceProvider provider = new LaunchServiceProvider("Red Energy",1989, "France");
            Launch l2  = new Launch(new Date(2000, 1, 23), rocket, provider,"London","GTO");

            fail("Launch date cannot earlier than rocket first launch year.");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Launch date cannot earlier than rocket first launch year.", e.getMessage().contains("Launch date"));
            System.out.println("testLaunchTimeEarlierThanRocketFirstLauchYear case pass.");
        }
    }

    @Test
    public void testSuccSetWikiLink(){
        launch.setWikilink("https://en.wikipedia.org/wiki/Comparison_of_orbital_launch_systems");

        assertEquals("https://en.wikipedia.org/wiki/Comparison_of_orbital_launch_systems", launch.getWikilink());
    }

    @Test
    public void testWikilinkValidWebsite(){
        try {
            launch.setWikilink("http://www.baidu.com");

            fail("Not WIKI URL. Please verify.");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("URL format is not wiki, but test pass.", e.getMessage().contains("WIKI"));
            System.out.println("testWikilinkValidWebsite case pass.");
        }
    }


    @Test
    public void testValidRocket() {
        LaunchServiceProvider lsp = new LaunchServiceProvider("Air space department", 2000, "China");
        dao.createOrUpdate(lsp);
        Rocket rocket = new Rocket("Chang E", "ShenZhou", "V", "China", lsp);

        Calendar calendar = new GregorianCalendar(2017, 10, 20);
        launch = new Launch(calendar.getTime(), rocket, lsp, "Jiu Quan", "LEO");


        try {
            dao.createOrUpdate(launch);
            assertNotNull(rocket.getId());
            //fail("Fail: rocket not exist, but launch created");
        } catch (IllegalArgumentException ex) {
            assertTrue("Throws IAE", ex instanceof IllegalArgumentException);
            assertTrue("rocket not exist..", ex.getMessage().contains("rocket not exist."));
            assertNull("launch saved", launch.getId());
            assertTrue("fail:should be no launch, but there is one.", dao.loadAll(Launch.class).size() == 0);
            System.out.println("testValidRocket case pass.");
        }
    }

    // lsp should exist when create new launch
    @Test
    public void testValidLSP() {
        LaunchServiceProvider lsp = new LaunchServiceProvider("Air space department", 2000, "China");
        dao.createOrUpdate(lsp);
        Rocket rocket = new Rocket("Chang E", "ShenZhou", "V", "China", lsp);
        dao.createOrUpdate(rocket);
        Calendar calendar = new GregorianCalendar(2017, 10, 20);
        LaunchServiceProvider fakeLsp = new LaunchServiceProvider("fakeLSP", 2001, "Australia");
        launch = new Launch(calendar.getTime(), rocket, fakeLsp, "Jiu Quan", "LEO");

        try {
            dao.createOrUpdate(launch);
            assertNotNull(lsp.getId());
            //fail("Fail: manufacturer not exist, but launch created");
        } catch (IllegalArgumentException ex) {
            assertTrue("Throws IAE", ex instanceof IllegalArgumentException);
            assertTrue("launch service provider not exist.", ex.getMessage().contains("launch service provider"));
            assertNull("launch saved", launch.getId());
            assertTrue("fail:should be no launch, but there is one.", dao.loadAll(Launch.class).size() == 0);
            System.out.println("testValidLSP case pass.");
        }
    }

}