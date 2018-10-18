package rockets.model;

import java.util.LinkedHashSet;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;
import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
@CompositeIndex(properties = {"name","familyName", "variationName", "country", "manufacturer"}, unique = true)
public class Rocket extends Entity {
    @Property(name="name")
    private String name;

    @Property(name="familyName")
    private String familyName;

    @Property(name="variationName")
    private String variationName;

    @Property(name="country")
    private String country;

    @Relationship(type = "MANUFACTURES", direction = INCOMING)
    private LaunchServiceProvider manufacturer;

    @Property(name="massToLEO")
    private String massToLEO;

    @Property(name="massToGTO")
    private String massToGTO;

    @Property(name="massToOther")
    private String massToOther;

    @Property(name="firstYearFlight")
    private int firstYearFlight;

    @Property(name="lastYearFlight")
    private int latestYearFlight;

    @Property(name="noLaunches")
    private int noLaunches;

    @Relationship(type = "PROVIDES", direction = OUTGOING)
    @JsonIgnore
    private Set<Launch> launches;

    public Rocket() {
        super();
    }

    /**
     * All parameters shouldn't be null.
     *
     * @param name
     * @param country
     * @param manufacturer
     */
    public Rocket(String name, String familyName, String variationName, String country, LaunchServiceProvider manufacturer) {
        notNull(name);
        notNull(country);
        notNull(manufacturer);
        notNull(familyName);
        notNull(variationName);

        if ("".equals(name) || "".equals(familyName) || "".equals(variationName)|| "".equals(country)){
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.familyName = familyName;
        this.variationName = variationName;
        this.country = country;
        this.manufacturer = manufacturer;

        this.launches = new LinkedHashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getVariationName(){
        return variationName;
    }

    public String getCountry() {
        return country;
    }

    public LaunchServiceProvider getManufacturer() {
        return manufacturer;
    }

    public String getMassToLEO() {
        return massToLEO;
    }

    public String getMassToGTO() {
        return massToGTO;
    }

    public String getMassToOther() {
        return massToOther;
    }

    public int getNoLaunches() {
        return noLaunches;
    }

    public int getFirstYearFlight() {
        return firstYearFlight;
    }

    public int getLatestYearFlight() {
        return latestYearFlight;
    }

    public void setMassToLEO(String massToLEO) {
        notNull(massToLEO);
        if ("".equals(massToLEO)){
            throw new IllegalArgumentException();
        }
        this.massToLEO = massToLEO;
    }

    public void setMassToGTO(String massToGTO) {
        notNull(massToGTO);
        if ("".equals(massToGTO)){
            throw new IllegalArgumentException();
        }
        this.massToGTO = massToGTO;
    }

    public void setMassToOther(String massToOther) {
        this.massToOther = massToOther;
    }

    public void setNoLaunches(int noLaunches) {
        notNull(noLaunches);
        if(noLaunches < 0 || this.firstYearFlight == 0 ) {
            throw new IllegalArgumentException();
        }

        if(this.firstYearFlight > 0 && noLaunches < 1) {
            throw new IllegalArgumentException();
        }

        if(this.latestYearFlight >= this.firstYearFlight && noLaunches < 2){
            throw new IllegalArgumentException();
        }

        this.noLaunches = noLaunches;
    }

    public void setCountry(String country)
    {
        if (null == country) {
            throw new NullPointerException("Country cannot be null");
        }
        if (country.trim().equals("")) {
            throw new IllegalArgumentException("Country cannot be empty");
        }
        this.country = country;
    }

    public void setFirstYearFlight(int firstYearFlight) throws IllegalArgumentException {
        notNull(firstYearFlight);
        if(firstYearFlight < 1957){
            throw new IllegalArgumentException();
        }

        if(this.latestYearFlight != 0 && firstYearFlight > this.latestYearFlight){
            throw new IllegalArgumentException();
        }

        this.firstYearFlight = firstYearFlight;
    }

    public void setLatestYearFlight(int latestYearFlight) {
        if(latestYearFlight < 1957){
            throw new IllegalArgumentException();
        }

        if (this.firstYearFlight == 0 || latestYearFlight < this.firstYearFlight) {
            throw new IllegalArgumentException();
        }
        this.latestYearFlight = latestYearFlight;
    }

    public Set<Launch> getLaunches() {
        return launches;
    }

    public void setLaunches(Set<Launch> launches) {
        this.launches = launches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(name, rocket.name) &&
                Objects.equals(country, rocket.country) &&
                Objects.equals(manufacturer, rocket.manufacturer) &&
                Objects.equals(familyName, rocket.familyName) &&
                Objects.equals(variationName, rocket.variationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, familyName, variationName, country, manufacturer);
    }

    @Override
    public String toString() {
        return "Rocket{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", massToLEO='" + massToLEO + '\'' +
                ", massToGTO='" + massToGTO + '\'' +
                ", massToOther='" + massToOther + '\'' +
                ", noLaunches=" + noLaunches +
                ", firstYearFlight=" + firstYearFlight +
                ", latestYearFlight=" + latestYearFlight +
                '}';
    }
}
