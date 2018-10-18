package rockets.mining;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.dataaccess.neo4j.Neo4jDAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.CombinableMatcher.either;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RocketMinerUnitTest {
    Logger logger = LoggerFactory.getLogger(RocketMinerUnitTest.class);

    private DAO dao;
    private RocketMiner miner;
    private List<Rocket> rockets;
    private List<LaunchServiceProvider> lsps;
    private List<Launch> launches;

    @Before
    public void setUp() {
        dao = mock(Neo4jDAO.class);
        miner = new RocketMiner(dao);

        rockets = Lists.newArrayList();

        lsps = Lists.newArrayList(
                new LaunchServiceProvider("ULA", 1990, "USA"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("ESA", 1975, "Europe ")
        );

        // index of lsp of each rocket
        int[] lspIndex    = new int[]{0, 0, 0, 1, 1};

        // 5 rockets
        for (int i = 0; i < 5; i++) {
            rockets.add(new Rocket("5" + i, "Ariane","ECT", "USA", lsps.get(lspIndex[i])));
        }
        when(dao.loadAll(Rocket.class)).thenReturn(rockets);

        Calendar calendar = new GregorianCalendar(2017, 01, 01);

        // month of each launch
        int[] months = new int[]{0, 5, 3, 2, 3, 11, 5, 4, 11, 4, 4, 4};

        // index of rocket of each launch
        int[] rocketIndex = new int[]{0, 0, 0, 0, 1, 1, 1, 0, 2, 0, 1, 1};

        // 12 launches
        launches = Lists.newArrayList();
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH, months[i]);
            logger.debug("Month is: " + calendar.get(Calendar.MONTH));
            LaunchServiceProvider lsp = null;
            if (i == 2 || i == 4) {
                lsp = lsps.get(0);
            } else if (i == 7 || i == 9) {
                lsp = lsps.get(0);
            } else if (i == 10 || i == 11) {
                lsp = lsps.get(1);
            } else if (i == 1 || i == 6) {
                lsp = lsps.get(0);
            } else {
                lsp = lsps.get(2);
            }
            Launch l = new Launch(calendar.getTime(), rockets.get(rocketIndex[i]), lsp, "Perlin","LEO");
            spy(l);
            launches.add(l);
        }
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        // set lauch price and outcome and launch site
        int basePrice = 10000;
        for (int i = 0; i < launches.size(); i++) {
            // get current loop launch
            Launch launch = launches.get(i);
            switch (i) {
                case 0:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.FAILED);
                    launch.setLaunchSite("Beijing");
                    break;
                case 1:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.FAILED);
                    launch.setLaunchSite("New York");
                    break;
                case 2:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.FAILED);
                    launch.setLaunchSite("Melbourne");
                    break;
                case 3:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("Mars");
                    break;
                case 4:
                    launch.setPrice(basePrice + 10000);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("Berlin");
                    break;
                case 5:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.FAILED);
                    launch.setLaunchSite("Dalian");
                    break;
                case 6:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("Beijing");
                    break;
                case 7:
                    launch.setPrice(basePrice + 10000);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("New York");
                    break;
                case 8:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("Seattle");
                    break;
                case 9:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("New York");
                    break;
                case 10:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("New York");
                    break;
                case 11:
                    launch.setPrice(basePrice);
                    launch.setLaunchOutcome(Launch.LaunchOutcome.SUCCESSFUL);
                    launch.setLaunchSite("Beijing");
                    break;
            }
        }
        rockets.get(0).setLaunches(Sets.newHashSet(launches.subList(0, 4)));
        rockets.get(1).setLaunches(Sets.newHashSet(launches.subList(4, 7)));
        rockets.get(2).setLaunches(Sets.newHashSet(launches.subList(7, 9)));
        rockets.get(3).setLaunches(Sets.newHashSet(launches.get(9)));
    }

    @Test
    public void testTopDifferentRockets() {
        int k = 1;

        List<Rocket> tops = miner.mostLaunchedRockets(k);
        assertEquals("correct number", 1, tops.size());
        assertEquals("correct top 1 rocket", rockets.get(0), tops.get(0));

        k = 2;
        tops = miner.mostLaunchedRockets(k);
        assertEquals("correct number", 2, tops.size());
        assertEquals("correct top 1 rocket", rockets.get(0), tops.get(0));
        assertEquals("correct top 1 rocket", rockets.get(1), tops.get(1));

        // Or alternatively
        assertThat("both top 2 correct", tops, equalTo(rockets.subList(0, 2)));

        k = 5;
        tops = miner.mostLaunchedRockets(k);
        assertEquals("correct number", 5, tops.size());

        k = 10;
        tops = miner.mostLaunchedRockets(k);
        assertEquals("correct number", 5, tops.size());
    }

    @Test
    public void testTopMostRecentLaunches() {
        int k = 1;

        List<Launch> loadedLaunches = miner.mostRecentLaunches(k);
        assertEquals("exactly 1 launch", 1, loadedLaunches.size());
        assertThat("contains either of last 2 launches", loadedLaunches,
                either(hasItem(launches.get(5))).or(hasItem(launches.get(8))));

        k = 2;
        loadedLaunches = miner.mostRecentLaunches(k);
        assertEquals("exactly 2 launches", 2, loadedLaunches.size());
        assertThat("contains both of last 2 launches", loadedLaunches,
                both(hasItem(launches.get(5))).and(hasItem(launches.get(8))));
    }

    @Test
    public void testBusiestLaunchServiceProviders() {
        int k = 2;

        List<LaunchServiceProvider> loadedLsps = miner.busiestLaunchServiceProviderInMonth(k, 2017, 4);
        assertEquals("only 1 in April", 1, loadedLsps.size());
        assertEquals("and it's ULA", lsps.get(0).getName(), loadedLsps.get(0).getName());

    }



    @Test
    public void testMostExpensiveLaunches() {
        int k = 1;
        List<Launch> loadedLaunches = miner.mostExpensiveLaunches(k);
        assertEquals("exactly 1 launch", 1, loadedLaunches.size());
        assertThat("contains either of last 2 launches", loadedLaunches,
                either(hasItem(launches.get(4))).or(hasItem(launches.get(7))));

        k = 2;
        loadedLaunches = miner.mostExpensiveLaunches(k);
        assertEquals("exactly 2 launches", 2, loadedLaunches.size());
        assertThat("contains both of last 2 launches", loadedLaunches,
                both(hasItem(launches.get(4))).and(hasItem(launches.get(7))));
    }

    @Test
    public void testMostUnreliableLaunchServiceProvider() {
        List <String> provider =  miner.mostUnreliableLaunchServiceProviders(1);
        assertEquals("most unrealiable provider",1,provider.size());
        assertEquals("The most unreliable provider is ULA", "ULA", provider.get(0).toString());
    }


    @Test
    public void testMostNumberofPlannedLaunchRockets(){
        Calendar calendar = new GregorianCalendar(2018, 8, 01);
        // date of planned launch
        int[] days = new int[] {2,3,4,5,6,7,8,9,10,11,12,13};
        // index of rocket of each launch
        int[] rocketIndex = new int[]{0, 0, 0, 0, 1, 1, 1, 0, 2, 0, 1, 1};

        // create launches
        launches = Lists.newArrayList();
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, days[i]);
            Launch l = new Launch(calendar.getTime(), rockets.get(rocketIndex[i]), lsps.get(0), "Perlin","LEO");
            rockets.get(rocketIndex[i]).getLaunches().add(l);
            spy(l);
            launches.add(l);
        }
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        int k = 6;

        List<Rocket> loadRockets = miner.mostNumberOfPlannedLaunch(k);

        assertEquals("exactly 1 rockets", 1, loadRockets.size());
        assertEquals("contains 1 rocket", loadRockets.get(0), rockets.get(0));

        k = 5;
        loadRockets = miner.mostNumberOfPlannedLaunch(k);
        assertEquals("exactly 2 rockets", 2, loadRockets.size());
        assertThat("contains both of last 2 rockets", loadRockets,
                both(hasItem(rockets.get(0))).and(hasItem(rockets.get(1))));
    }

}