package rockets.mining;

import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
import java.util.*;
import java.util.stream.Collectors;

public class RocketMiner {
    private static Logger logger = LoggerFactory.getLogger(RocketMiner.class);

    private DAO dao;

    public RocketMiner(DAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the top-k active rocket, as measured by number of launches.
     *
     * @param k the number of rockets to be returned.
     * @return the list of k most active rockets.
     */
    public List<Rocket> mostLaunchedRockets(int k) {
        Collection<Rocket> rockets = dao.loadAll(Rocket.class);
        //logger.debug("Getting all rockets, total = " + rockets.size());

        ListMultimap<Integer, Rocket> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        for (Rocket r : rockets) {
            Set<Launch> launches = r.getLaunches();
            if (null != launches) {
                multimap.put(launches.size(), r);
            } else {
                multimap.put(0, r);
            }
        }
        List<Integer> sortedKeys = Lists.newArrayList(multimap.keySet());
        Collections.sort(sortedKeys, Ordering.natural().reverse());

        List<Rocket> result = Lists.newArrayList();
        for (Integer key : sortedKeys) {
            List<Rocket> list = multimap.get(key);
            if (result.size() >= k) {
                break;
            } else if (result.size() + list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
            } else {
                result.addAll(list);
            }
        }

        return result;
    }

    /**
     * TODO: to be implemented & tested!
     *
     * Returns the top-k most unreliable launch service providers as measured
     * by percentage of failed launches.
     *
     * @param k the number of launch service providers to be returned.
     * @return the list of k most unreliable ones.
     */
    public List<String> mostUnreliableLaunchServiceProviders(int k) {
        Collection<Launch> LaunchList3 = dao.loadAll(Launch.class);
        List<Launch> LaunchList = new ArrayList<Launch>(LaunchList3);

        List<Launch> filteredLaunchList= LaunchList.stream().filter(Launch -> Launch.getLaunchOutcome() == rockets.model.Launch.LaunchOutcome.FAILED).collect(Collectors.toList());

        Map<String, Long> counts = filteredLaunchList.stream().collect(Collectors.groupingBy(o -> o.getLaunchVehicle().getManufacturer().getName(),Collectors.counting()));

        List<String> sortedKeys2=new ArrayList(counts.keySet());

        SortedSet<String> values = new TreeSet<>(counts.keySet());
        ArrayList<Long> l = new ArrayList<Long>();
        for (String value : values) {
            Long valuess = counts.get(value);
            l.add(valuess);
        }

        String s = sortedKeys2.get(0); // remember first item
        sortedKeys2.clear(); // clear complete list
        sortedKeys2.add(s); // add first item
        return sortedKeys2;
    }

    /**
     * TODO: to be implemented & tested!
     *
     * Returns the top-k most recent launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most recent launches.
     */
    public List<Launch> mostRecentLaunches(int k) {
        Collection<Launch> launches = dao.loadAll(Launch.class);
        logger.debug("Getting all launches, total = " + launches.size());

        ListMultimap<Date, Launch> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        for (Launch l : launches) {
            multimap.put(l.getLaunchDate(), l);
        }

        List<Date> sortedKeys = Lists.newArrayList(multimap.keySet());
        Collections.sort(sortedKeys, Ordering.natural().reverse());

        List<Launch> result = Lists.newArrayList();
        List<Launch> list ;
        List<Launch> finalist = Lists.newArrayList();
        for (Date key : sortedKeys) {
            list = multimap.get(key);

            if(list.size() > 1)
            {
                for(int i = 0 ; i < list.size(); i++)
                {
                    finalist.add(list.get(i));
                }
            }
            else
                finalist.add(list.get(0));
        }

        for (int i=0; i<k ; i++ )
        {
            result.add(finalist.get(i));
        }

        return result;
    }

    /**
     * TODO: to be implemented & tested!
     *
     * Returns the top-k busiest launch service provides as measured by the
     * number of launches in <code>month</code> and </code><code>year</code>.
     *
     * @param k the number of launch service providers to be returned.
     * @param year the year
     * @param month the month of the year
     * @return the list of k busiest launch service providers.
     */

    public List<LaunchServiceProvider> busiestLaunchServiceProviderInMonth(int k, int year, int month) {
        Collection<Launch> launchList = dao.loadAll(Launch.class);
        logger.debug("Getting all launches, total = " + launchList.size());

        // Extracting Launch list in a given month and year
        List<Launch> matchedLaunchList = Lists.newArrayList();
        Calendar cal = Calendar.getInstance();
        for (Launch l : launchList){
            cal.setTime(l.getLaunchDate());
            //System.out.println(l.getLaunchDate().getTime().toString());
            if (cal.get(Calendar.YEAR) == year)
                if (cal.get(Calendar.MONTH) == month - 1)
                    matchedLaunchList.add(l);
        }
        //System.out.println("Matched Launch List:" + matchedLaunchList.size());
        //for (Launch l: matchedLaunchList){
        //    System.out.println(l.getLaunchVehicle().getManufacturer().getName());
        //}

        // Extract Rocket list based on Launch
        List<Rocket> matchedRocketList = Lists.newArrayList();
        for (Launch l : matchedLaunchList){
            matchedRocketList.add(l.getLaunchVehicle());
        }
        //System.out.println("Matched Rocket List:" + matchedRocketList.size());
        //for (Rocket r: matchedRocketList){
        //   System.out.println(r.getManufacturer().getName());
        //

        // Extract Launch Service Provider List based on Rocket
        List<LaunchServiceProvider> matchedLSPList = Lists.newArrayList();
        for (Rocket r : matchedRocketList) {
            matchedLSPList.add(r.getManufacturer());
        }
        //System.out.println("Matched LSP List:" + matchedLSPList.size());
        //for (LaunchServiceProvider lsp: matchedLSPList){
        //   System.out.println(lsp.getName());
        //}

        // Count for each LSP
        Map<LaunchServiceProvider, Long> lspCount = matchedRocketList.stream().collect(Collectors.groupingBy(o -> o.getManufacturer() ,Collectors.counting()));

        //System.out.println("This is all LSP" + sortedLSPCountValues.size());
        lspCount.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));

        List<LaunchServiceProvider> result = new ArrayList<>();
        for (Map.Entry<LaunchServiceProvider, Long> entry: lspCount.entrySet()){
            if (result.size() >= k)
                break;
            else
                result.add(entry.getKey());
        }
        return result;
    }


    /**
     * TODO: to be implemented & tested!
     *
     * Returns the top-k most expensive launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most expensive launches.
     */
    public List<Launch> mostExpensiveLaunches(int k) {
        Collection<Launch> launches = dao.loadAll(Launch.class);

        ListMultimap<Integer, Launch> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        for (Launch l : launches) {
            multimap.put(l.getPrice(), l);
        }

        List<Integer> sortedKeys = Lists.newArrayList(multimap.keySet());
        Collections.sort(sortedKeys, Ordering.natural().reverse());

        List<Launch> result = Lists.newArrayList();
        List<Launch> list;
        List<Launch> finalist = Lists.newArrayList();

        for (Integer key : sortedKeys){
            list = multimap.get(key);

            if(list.size() > 1)
            {
                for(int i = 0 ; i < list.size(); i++)
                {
                    finalist.add(list.get(i));
                }
            }
            else
                finalist.add(list.get(0));
        }

        String rocketName = "";
        int totalLaunch = 0;

        for (Launch rocket : finalist)
        {
            if(!(rocketName.equalsIgnoreCase(rocket.getLaunchVehicle().getName())))
            {
                totalLaunch = totalLaunch + 1;
                result.add(rocket);
                rocketName = rocket.getLaunchVehicle().getName();
            }

            if(result.size() == k )
                break;
        }

        return result;
    }


    public List<Rocket> mostNumberOfPlannedLaunch(int k){
        List<Rocket> result = new ArrayList<>();

        // get all launches
        Collection<Launch> allLaunches = dao.loadAll(Launch.class);
        // get planned launches
        List<Launch> loadPlanLaunches = new ArrayList<>();
        for (Launch launch : allLaunches) {
            if (launch.getLaunchDate().after(new Date())) {
                loadPlanLaunches.add(launch);
            }
        }

        // calculate how many planned times for each planned rocket
        Map<Rocket, Integer> dicRockets = new LinkedHashMap<>();
        for (Launch launch : loadPlanLaunches) {
            Rocket temp  = launch.getLaunchVehicle();
            if (!dicRockets.containsKey(temp)) {
                dicRockets.put(temp, 1);
            } else {
                dicRockets.replace(temp, dicRockets.get(temp) + 1);
            }
        }

        // construct result
        for (Map.Entry<Rocket, Integer> item : dicRockets.entrySet()) {
            if (item.getValue() >= k)
                result.add(item.getKey());
        }

        return result;
    }
}
