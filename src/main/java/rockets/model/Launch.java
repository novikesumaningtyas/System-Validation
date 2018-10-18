package rockets.model;

import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
@CompositeIndex(properties = {"launchDate", "launchVehicle", "launchSite", "orbit"}, unique = true)
public class Launch extends Entity {

    public enum LaunchOutcome {
        FAILED, SUCCESSFUL
    }

    @DateLong
    private Date launchDate;

    @Relationship(type = "PROVIDES", direction = INCOMING)
    private Rocket launchVehicle;

    private LaunchServiceProvider launchServiceProvider;

    private Set<String> payload;

    @Property(name = "launchSite")
    private String launchSite;

    @Property(name = "orbit")
    private String orbit;

    @Property(name = "function")
    private String function;

    @Property(name = "launchOutcome")
    private LaunchOutcome launchOutcome;

    @Property(name = "price")
    private int price;

    public Launch() {
        super();
    }

    public Launch(Date launchDate, Rocket launchVehicle, LaunchServiceProvider launchServiceProvider, String launchSite, String orbit) throws IllegalArgumentException {
        notNull(launchDate);
        notNull(launchVehicle);
        notNull(launchServiceProvider);
        notNull(launchSite);
        notNull(orbit);

        this.launchDate = launchDate;
        if (launchDate.getYear() < launchVehicle.getFirstYearFlight()) {
            throw new IllegalArgumentException("Launch date cannot earlier than rocket first launch year.");
        }

        this.launchVehicle = launchVehicle;
        this.launchServiceProvider = launchServiceProvider;
        this.launchSite = launchSite;
        this.orbit = orbit;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public Rocket getLaunchVehicle() {
        return launchVehicle;
    }

    public LaunchServiceProvider getLaunchServiceProvider() {
        return launchServiceProvider;
    }

    public Set<String> getPayload() {
        return payload;
    }

    public String getLaunchSite() {
        return launchSite;
    }

    public String getOrbit() {
        return orbit;
    }

    public String getFunction() {
        return function;
    }

    public LaunchOutcome getLaunchOutcome() {
        return launchOutcome;
    }

    public void setPayload(Set<String> payload) {
        this.payload = payload;
    }

    public void setLaunchSite(String launchSite) {
        if ("" == launchSite){
            throw new IllegalArgumentException("Launch site cannot be empty.");
        }

        this.launchSite = launchSite;
    }

    public void setFunction(String function) {
        notNull(function);

        if ("" == function){
            throw new IllegalArgumentException("function cannot be empty.");
        }
        this.function = function;
    }

    public void setLaunchOutcome(LaunchOutcome launchOutcome) {
        notNull(launchOutcome);

        boolean isExistInEnum = false;
        for (LaunchOutcome c : LaunchOutcome.values()) {
            if (c.equals(launchOutcome)) {
                isExistInEnum =  true;
            }
        }

        if (!isExistInEnum){
            throw new IllegalArgumentException("Outcome must be FAILED or SUCCESSFUL.");
        }
        this.launchOutcome = launchOutcome;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Launch launch = (Launch) o;
        return Objects.equals(launchDate, launch.launchDate) &&
                Objects.equals(launchVehicle, launch.launchVehicle) &&
                Objects.equals(launchServiceProvider, launch.launchServiceProvider) &&
                Objects.equals(orbit, launch.orbit);
    }

    @Override
    public int hashCode() {

        return Objects.hash(launchDate, launchVehicle, launchServiceProvider, orbit);
    }
}
