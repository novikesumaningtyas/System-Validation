package rockets.dataaccess.neo4j;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.test.rule.TestDirectory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
import rockets.model.User;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Neo4jDAOUnitTest {
    private static final String TEST_DB = "target/test-data/test-db";

    @ClassRule
    public static TestDirectory testDirectory = TestDirectory.testDirectory();

    private static DAO dao;
    private static Session session;
    private static SessionFactory sessionFactory;

    private static LaunchServiceProvider esa;
    private static LaunchServiceProvider spacex;
    private Launch launch;
    private Rocket rocket;
    private Rocket rocket2;
    private static Calendar calendar;

    private static User user;

    @BeforeClass
    public static void setUp() {
        EmbeddedDriver driver;

        //Comment to follow code from fit5171_2018_17
        //driver = createEmbeddedDriver(false, TEST_DB);
        driver = createImpermanentEmbeddedDriver(TEST_DB);

        sessionFactory = new SessionFactory(driver, User.class.getPackage().getName());
        session = sessionFactory.openSession();

        dao = new Neo4jDAO(sessionFactory);

        //follow code
        calendar = new GregorianCalendar(2017, 01, 01);


    }


    //follow code
    private static EmbeddedDriver createImpermanentEmbeddedDriver(String fileDir) {
        GraphDatabaseService db = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder(testDirectory.keepDirectoryAfterSuccessfulTest().directory(fileDir))
                .setConfig(GraphDatabaseSettings.pagecache_memory, "64M")
                .newGraphDatabase();
        return new EmbeddedDriver(db);
    }

    private static EmbeddedDriver createEmbeddedDriver(boolean temporary, String fileDir) {
        if (temporary) {
            GraphDatabaseService db = new TestGraphDatabaseFactory()
                    .newImpermanentDatabaseBuilder(testDirectory.directory(fileDir))
                    .setConfig(GraphDatabaseSettings.pagecache_memory, "64M")
                    .newGraphDatabase();
            return new EmbeddedDriver(db);
        } else {
            File file = new File(fileDir);
            Configuration configuration = new Configuration.Builder()
                    //.uri(neoServer.boltURI().toString()) // For Bolt
                    //.uri(neoServer.httpURI().toString()) // For HTTP
                    .uri(file.toURI().toString()) // For Embedded
                    .build();
            EmbeddedDriver driver = new EmbeddedDriver();
            driver.configure(configuration);
            return driver;
        }

    }

    @Before
    public void localSetUp() {
        esa = new LaunchServiceProvider("ESA", 1970, "Europe");
        spacex = new LaunchServiceProvider("SpaceX", 2002, "USA");

        rocket = new Rocket("F9", "Ariane", "ECA", "USA", spacex);
        rocket2 = new Rocket("A5", "Ariane", "ECA", "Europe", esa);
        user = new User();
    }

    @Test
    public void daoSuccessfullyCreated() {
        assertNotNull("dao is not null", dao);
    }

    @Test
    public void successfulCreateRocket() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");

        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull("ID not null", graphRocket.getId());
        assertEquals("Same rocket", rocket, graphRocket);
        LaunchServiceProvider manufacturer = graphRocket.getManufacturer();
        assertNotNull("ID not null", manufacturer.getId());
        assertEquals("same wikilink", rocket.getWikilink(), graphRocket.getWikilink());
        assertEquals("Same LSP", spacex, manufacturer);
    }

    @Test
    public void rocketAttributesCanBeUpdated() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");

        //follow code
        dao.createOrUpdate(spacex);

        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull("ID not null", graphRocket.getId());
        assertEquals("Same rocket", rocket, graphRocket);

        //follow code
        //String newLink = "http://adifferentlink.com";
        String newLink = "http://en.wikipedia.org";

        rocket.setWikilink(newLink);
        dao.createOrUpdate(rocket);
        graphRocket = dao.load(Rocket.class, rocket.getId());
        assertEquals("a different link", newLink, graphRocket.getWikilink());
    }

    @Test
    public void sameRocketCannotBeSavedTwice() {
        assertNull("LSP doesn't have ID", spacex.getId());

        //follow code
        LaunchServiceProvider lsp1 = new LaunchServiceProvider("LSP 1", 2002, "USA");
        dao.createOrUpdate(lsp1);

        Rocket rocket1 = new Rocket("F9", "Ariane", "ECA", "USA", lsp1);
        Rocket rocket2 = new Rocket("F9", "Ariane", "ECA", "USA", lsp1);
        //Rocket rocket1 = new Rocket("F9", "USA", spacex);
        //Rocket rocket2 = new Rocket("F9", "USA", spacex);

        assertEquals(rocket1, rocket2);

        dao.createOrUpdate(rocket1);

        //assertNotNull("LSP has ID", spacex.getId());
        assertNotNull("LSP has ID", lsp1.getId());

        Collection<Rocket> rockets = dao.loadAll(Rocket.class);
        assertEquals(1, rockets.size());
        Collection<LaunchServiceProvider> manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals("should have 1 LSP", 1, manufacturers.size());

        dao.createOrUpdate(rocket2);

        manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals("should still have 1 LSP", 1, manufacturers.size());

        rockets = dao.loadAll(Rocket.class);
        assertEquals("only 1 rocket", 1, rockets.size());
    }

    @Test
    public void userWithSameEmailCannotBeSavedTwice(){
        assertNull("There is no user", user.getId());

        user = new User("abc@example.com");
        User user2 = new User("abc@example.com");

        assertEquals(user, user2);

        dao.createOrUpdate(user);

        assertNotNull("user has ID", user.getId());

        Collection<User> users = dao.loadAll(User.class);
        assertEquals(1, users.size());

        dao.createOrUpdate(user2);

        users = dao.loadAll(User.class);
        assertEquals("should still have 1 user", 1, users.size());

        User user3 = new User("abcd@example.com");

        dao.createOrUpdate(user3);

        users = dao.loadAll(User.class);
        assertEquals("should have 2 user", 2, users.size());
    }

    @Test
    public void canSaveLoad3Rockets() {

        //follow code
        Set<Rocket> rockets = Sets.newHashSet(
                new Rocket("Ariane4", "Ariane", "ECA", "France", esa),
                new Rocket("F5", "Frank", "ECA", "USA", spacex),
                new Rocket("BFR", "Ballo", "ECA", "USA", spacex)
        );

        dao.createOrUpdate(esa);
        dao.createOrUpdate(spacex);

        /*Set<Rocket> rockets = Sets.newHashSet(
                new Rocket("Ariane4", "France", esa),
                new Rocket("F5", "USA", spacex),
                new Rocket("BFR", "USA", spacex)
        );*/

        for (Rocket r : rockets) {
            dao.createOrUpdate(r);
        }

        Collection<Rocket> loadedRockets = dao.loadAll(Rocket.class);
        assertEquals("same size", rockets.size(), loadedRockets.size());
        for (Rocket r : rockets) {
            assertTrue("contains " + r.getName(), rockets.contains(r));
        }
    }

    @Test
    public void testSaveUniqueLaunchTwiceOnlySavesOne() {
        Calendar calendar = new GregorianCalendar(2017, 01, 01);
        //Launch launch = new Launch(calendar.getTime(), rocket, "VAFB", "LEO");
        //follow code
        dao.createOrUpdate(spacex);
        dao.createOrUpdate(rocket);
        Launch launch = new Launch(calendar.getTime(), rocket, spacex, "Perlin", "LEO");

        dao.createOrUpdate(launch);

        Collection<Launch> launches = dao.loadAll(Launch.class);
        assertTrue("Exists", !launches.isEmpty());
        assertTrue("Contains launch", launches.contains(launch));

        Launch loadedLaunch = launches.iterator().next();
        assertNull("null function", loadedLaunch.getFunction());

        launch.setFunction("experimental");
        dao.createOrUpdate(launch);

        launches = dao.loadAll(Launch.class);
        assertEquals("only 1 launch", 1, launches.size());
        loadedLaunch = launches.iterator().next();
        assertEquals("correct function", "experimental", loadedLaunch.getFunction());
    }

    @Test
    public void saveARocketBeforeALSPDoesAcrossSessionsNotCreateDuplicateRockets() {
        assertEquals("Same LSP", spacex, rocket.getManufacturer());
        spacex.getRockets().add(rocket);
        dao.createOrUpdate(spacex);
        assertEquals("Only 1 rocket", 1, dao.loadAll(Rocket.class).size());

        dao.close();

        setUp();

        rocket.setId(null);
        spacex.setId(null);
        dao.createOrUpdate(spacex);
        assertEquals("Only 1 rocket", 1, dao.loadAll(Rocket.class).size());
    }

   /* @Test
    public void testDeleteRocketWillNotDeleteLSP() {
        //follow code
        dao.createOrUpdate(spacex);

        dao.createOrUpdate(rocket);

        assertNotNull("rocket saved", rocket.getId());
        assertNotNull("LSP saved", rocket.getManufacturer().getId());

        assertFalse("rocket exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("LSP exists", dao.loadAll(LaunchServiceProvider.class).isEmpty());

        dao.delete(rocket);

        assertTrue("rocket no longer exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("LSP still exists", dao.loadAll(LaunchServiceProvider.class).isEmpty());

        //follow code
        assertFalse("LSP not contain this rocket any more", spacex.getRockets().contains(rocket));
    }*/

    //follow code

    @Test
    public void testDeleteLSPWillNotDeleteRocket() {
        dao.createOrUpdate(spacex);
        dao.createOrUpdate(rocket);

        assertNotNull("rocket saved", rocket.getId());
        assertNotNull("LSP saved", rocket.getManufacturer().getId());

        assertFalse("rocket exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("LSP exists", dao.loadAll(LaunchServiceProvider.class).isEmpty());

        dao.delete(rocket.getManufacturer());

        assertFalse("rocket no longer exists", dao.loadAll(Rocket.class).isEmpty());
        assertTrue("LSP still exists", dao.loadAll(LaunchServiceProvider.class).isEmpty());
    }

    @Test
    public void testDeleteCreatedRocket()
    {
        dao.createOrUpdate(rocket2);
        Collection<Rocket> rockets = dao.loadAll(Rocket.class);

        assertNotNull("rocket2 saved", rocket2.getId());
        assertFalse("rocket2 exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("LSP exists", dao.loadAll(LaunchServiceProvider.class).isEmpty());
        assertEquals("There is one rocket saved",1,rockets.size());

        dao.delete(rocket2);
        rockets = dao.loadAll(Rocket.class);
        assertTrue("rocket2 no longer exists", dao.loadAll(Rocket.class).isEmpty());
        assertEquals("There is no saved rocket",0, rockets.size() );
    }

  /*  @Test
    public void testDeleteRocket() {
        dao.createOrUpdate(spacex);
        spacex.getRockets().add(rocket);
        dao.createOrUpdate(rocket);
        assertNotNull("rocket saved", rocket.getId());
        dao.delete(rocket);
        assertTrue("rocket no longer exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("rocket LSP not contain this rocket any more", spacex.getRockets().contains(rocket));
    }*/

    @Test
    public void testDeleteLSP() {
        dao.createOrUpdate(spacex);
        dao.createOrUpdate(rocket);
        assertNotNull("LSP saved", dao.loadAll(LaunchServiceProvider.class).isEmpty());
        dao.delete(rocket.getManufacturer());
        assertTrue("LSP no longer exists", dao.loadAll(LaunchServiceProvider.class).isEmpty());
    }

    /*@Test
    public void testDeleteLaunch() {
        dao.createOrUpdate(spacex);
        launch = new Launch(calendar.getTime(), rocket, spacex, "Washington DC", "LEO");
        rocket.setLaunches(Sets.newHashSet(launch));
        dao.createOrUpdate(rocket);
        dao.createOrUpdate(launch);
        assertNotNull("launch saved", dao.loadAll(Launch.class).isEmpty());
        dao.delete(launch);
        assertTrue("launch no longer exists", dao.loadAll(Launch.class).isEmpty());
        assertFalse("rocket no longer contain launch", rocket.getLaunches().contains(launch));
    }*/

    /*@Test
    public void testDeleteLaunchWillNotDeleteRocket() {
        dao.createOrUpdate(spacex);
        launch = new Launch(calendar.getTime(), rocket, spacex, "Washington DC", "LEO");
        rocket.setLaunches(Sets.newHashSet(launch));
        dao.createOrUpdate(rocket);
        dao.createOrUpdate(launch);

        assertNotNull("launch saved", launch.getId());
        assertNotNull("rocket saved", launch.getLaunchVehicle().getId());

        assertFalse("rocket exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("launch exists", dao.loadAll(Launch.class).isEmpty());

        dao.delete(launch);

        assertTrue("launch no longer exists", dao.loadAll(Launch.class).isEmpty());
        assertFalse("rocket still exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("rocket launch set not contain the deleted launch", rocket.getLaunches().contains(launch));
    }
*/
    @Test
    public void testDeleteRocketWillNotDeleteLaunch(){
        dao.createOrUpdate(spacex);
        launch = new Launch(calendar.getTime(), rocket, spacex, "Washington DC", "LEO");
        dao.createOrUpdate(rocket);
        rocket.setLaunches(Sets.newHashSet(launch));
        dao.createOrUpdate(launch);

        assertNotNull("launch saved", launch.getId());
        assertNotNull("rocket saved", launch.getLaunchVehicle().getId());

        assertFalse("rocket exists", dao.loadAll(Rocket.class).isEmpty());
        assertFalse("launch exists", dao.loadAll(Launch.class).isEmpty());

        dao.delete(rocket);

        assertFalse("launch still exists", dao.loadAll(Launch.class).isEmpty());
        assertTrue("rocket no longer exists", dao.loadAll(Rocket.class).isEmpty());
    }

    @Test
    public void testDeleteUser(){
        User user = new User("swan0005@student.monash.edu");
        dao.createOrUpdate(user);
        assertNotNull("user saved", user.getId());
        assertFalse("user exists", dao.loadAll(User.class).isEmpty());
        dao.delete(user);
        assertTrue("user no longer exist", dao.loadAll(User.class).isEmpty());
    }



    @After
    public void localTearDown() {
        session.purgeDatabase();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        dao.close();

        //follow code
        session.clear();
        sessionFactory.close();
        File testDir = new File(TEST_DB);
        FileUtils.deleteDirectory(testDir);
    }
}