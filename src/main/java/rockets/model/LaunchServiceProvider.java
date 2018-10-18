package rockets.model;

import com.google.common.collect.Sets;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;

@NodeEntity
@CompositeIndex(properties = {"name", "yearFounded", "country"}, unique = true)
public class LaunchServiceProvider extends Entity {
    @Property(name = "name")
    private String name;

    @Property(name = "yearFounded")
    private int yearFounded;

    @Property(name = "country")
    private String country;

    @Property(name = "headquarters")
    private String headquarters;

    @Relationship(type = "MANUFACTURES", direction= OUTGOING)
    @JsonIgnore
    private Set<Rocket> rockets;

    public LaunchServiceProvider() {
        super();
    }

    public LaunchServiceProvider(String name, int yearFounded, String country) {
        notNull(name);
        notNull(yearFounded);
      //  notNull(country);

//        if (yearFounded < 1957) {
//            throw new IllegalArgumentException("Year is illegal.");
//        }

        this.name = name;
        this.yearFounded = yearFounded;
        this.country = country;

        rockets = Sets.newLinkedHashSet();
    }

    public void setCountry(String country)
    {
        if (null == country || ""== country) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }

        this.country = country;
    }

    public String getName() {
        return name;
    }

    public int getYearFounded() {
        return yearFounded;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public Set<Rocket> getRockets() {
        return rockets;
    }



    public void setHeadquarters(String headquarters) {
        if (headquarters == null || "" == headquarters){
            throw new IllegalArgumentException("Headquarters cannot be null or empty.");
        }

        this.headquarters = headquarters;
    }

    public void setRockets(Set<Rocket> rockets) {
        if (null == rockets){
            throw new IllegalArgumentException("rockets cannot be null");
        }
        this.rockets = rockets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaunchServiceProvider that = (LaunchServiceProvider) o;
        return yearFounded == that.yearFounded &&
                Objects.equals(name, that.name) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, yearFounded, country);
    }
}
