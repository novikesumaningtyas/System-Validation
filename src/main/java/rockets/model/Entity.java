package rockets.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;

public abstract class Entity {
    @Id
    @GeneratedValue
    protected Long id;

    @Property(name = "wikilink")
    private String wikilink;

    public Entity() {
    }

    public String getWikilink() {
        return wikilink;
    }

    public void setWikilink(String wikilink) throws IllegalArgumentException {
        if (!wikilink.contains("en.wikipedia.org")) {
            throw new IllegalArgumentException("Not WIKI URL. Please verify.");
        }
        this.wikilink = wikilink;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
