package rockets.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class UserUnitTest {
    private User p;

    @Before
    public void setUp() {
        p = new User("abc@example.com");
    }

    @Test(expected = NullPointerException.class)
    public void testNullEmailNotAllowed() {
        p = new User(null);
    }

    @Test
    public void equalEmailMeansSameUser() {
        User p1 = new User("abc@example.com");
        assertEquals("same user", p, p1);
    }

    @Test
    public void differentEmailsMeansDifferentUser() {
        User p1 = new User("def@foo.org");

        assertNotEquals("different users", p, p1);
    }

    @Test
    public void emailFormatValid(){
        try{
            User p1 = new User("email1@123cn");
            fail("Email format is invalid. Must contain at least 1 '@' and end with '.' following by domain.");
        } catch (Exception e){
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Email format is invalid.", e.getMessage().contains("wrong"));
            System.out.println("emailFormatValid case pass.");
        }
    }

    @Test
    public void differentEmailhasDiffHashCode(){
        User u1 = new User("email1@163.com");
        User u2 = new User("email2@163.com");

        assertNotEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void sameEmailHasSameHashcode(){
        User p1 = new User("abc@example.com");
        assertEquals("Same user, same hashcode", p.hashCode(), p1.hashCode());
    }

    @Test
    public void passwordNotNullOrEmpty() {
        try {
            p.setPassword(null);
            fail("Password cannot be null");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains null", e.getMessage().contains("null"));
            System.out.println("passwordNotNullOrEmpty case pass.");
        }

        try {
            p.setPassword("");
            fail("Password cannot be empty");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains empty", e.getMessage().contains("empty"));
            System.out.println("passwordNotNullOrEmpty case pass.");
        }
    }

    @Test
    public void testStrongPassword(){
        String newPwd = "P@ssw0rd";
        p.setPassword(newPwd);
        assertEquals(newPwd, p.getPassword());
    }

    /*@Test
    public void testWeakPassword(){
        String newPwd = "P@ssword";

        // Test no number
        try {
            p.setPassword(newPwd);
            fail("Password not have number.");
        } catch(Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Password not have number.", e.getMessage().contains("strong enough"));
            System.out.println("Password have number case pass.");
        }

        // Test no special character
        try{
            newPwd = "Passw0rd";
            p.setPassword(newPwd);
            fail("Password not have special character.");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Password not have special character.", e.getMessage().contains("strong enough"));
            System.out.println("Password have special character case pass.");
        }

        // Test no lower letter
        try{
            newPwd = "P@SSW0RD";
            p.setPassword(newPwd);
            fail("Password not have lower letter.");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Password not have lower letter.", e.getMessage().contains("strong enough"));
            System.out.println("Password have special character case pass.");
        }

        // Test no upper letter
        try{
            newPwd = "p@ssw0rd";
            p.setPassword(newPwd);
            fail("Password not have upper letter.");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Password not have upper letter.", e.getMessage().contains("strong enough"));
            System.out.println("Password have upper letter case pass.");
        }

        // Test less than 8 length
        try{
            newPwd = "P@ssw0r";
            p.setPassword(newPwd);
            fail("Password less than 8 digits.");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Password less than 8 digits.", e.getMessage().contains("strong enough"));
            System.out.println("Password length test case pass.");
        }
    }*/

    @Test(expected = NullPointerException.class)
    public void testNullFNameNotAllowed(){
        p.setFirstName(null);
    }

    @Test
    public void testEmptyFNameNotAllowed(){
        String firstName = "";

        try {
            p.setFirstName(firstName);
            fail("First name is empty.");
        } catch(Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("First name is empty.", e.getMessage().contains("empty"));
            System.out.println("testEmptyFNameNotAllowed case pass.");
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullLNameNotAllowed(){
        p.setLastName(null);
    }

    @Test
    public void testEmptyLNameNotAllowed(){
        String lastName = "";

        try {
            p.setLastName(lastName);
            fail("Last name is empty.");
        } catch(Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Last name is empty.", e.getMessage().contains("empty"));
            System.out.println("testEmptyLNameNotAllowed case pass.");
        }
    }

    @Test
    public void testSameUserSameToString(){
        User u1 = new User("email1@163.com");
        User u2 = new User("email1@163.com");

        // Initial names
        u1.setFirstName("Tom");
        u1.setLastName("Hanks");
        u2.setFirstName("Tom");
        u2.setLastName("Hanks");

        assertEquals("Same user, same toString result", u1.toString(), u2.toString());
    }

    @Test
    public void diffLNameDiffToString(){
        User u1 = new User("email1@163.com");
        User u2 = new User("email1@163.com");

        // Initial names
        u1.setFirstName("Tom");
        u1.setLastName("Hanks");
        u2.setFirstName("Tom");
        u2.setLastName("Grimm");

        assertNotEquals("Different last name, different toString result", u1.toString(), u2.toString());
    }

    @Test
    public void diffFNameDiffToString(){
        User u1 = new User("email1@163.com");
        User u2 = new User("email1@163.com");

        // Initial names
        u1.setFirstName("Tom");
        u1.setLastName("Hanks");
        u2.setFirstName("Geo");
        u2.setLastName("Hanks");

        assertNotEquals("Different first name, different toString result", u1.toString(), u2.toString());
    }

    @Test
    public void diffFullNameDiffToString(){
        User u1 = new User("email1@163.com");
        User u2 = new User("email1@163.com");

        // Initial names
        u1.setFirstName("Jenny");
        u1.setLastName("Hanks");
        u2.setFirstName("Kari");
        u2.setLastName("Sylvanas");

        assertNotEquals("Different full name, different toString result", u1.toString(), u2.toString());
    }
}