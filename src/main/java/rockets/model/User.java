package rockets.model;

import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
@CompositeIndex(properties = {"email"}, unique = true)
public class User extends Entity {
    @Property(name="firstName")
    private String firstName;
    @Property(name="lastName")
    private String lastName;
    @Property(name="email")
    private String email;
    @Property(name="password")
    private String password;


    public User() {
       /* this.email = "undefined@undefined.undefined";
        this.firstName = "undefined";
        this.lastName = "undefined";
        this.password = "undefined";*/
    }

    public User(String email) {
        notNull(email);

        if(!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*+@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
            throw new IllegalArgumentException("Email format is wrong. Must contain at least 1 '@' and end with '.' following by domain");
        }
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() { return password; }

    public void setFirstName(String firstName) throws IllegalArgumentException {
        notNull(firstName);

        if ("" == firstName.trim() || "".equals(firstName.trim())){
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        notNull(lastName);

        if ("" == lastName.trim() || "".equals(lastName.trim())){
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        this.lastName = lastName;
    }

    public void setPassword(String password) {
        if (null == password) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (password.trim().equals("")) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // check if the string is a strong password. At least 8 length. Contains at least 1 special character, 1 lower&upper letter, and 1 number
        /*if(!password.trim().matches("^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8}$")){
            throw new IllegalArgumentException("Password not strong enough.");
        }*/

        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        // return hashcode
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
