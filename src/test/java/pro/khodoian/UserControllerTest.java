package pro.khodoian;

import org.junit.Test;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.UserControllerTestServiceApi;
import pro.khodoian.models.User;
import retrofit.client.ApacheClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class for testing UserController
 * Shall be run, when Application is running in background
 *
 * @author eduardkhodoyan
 */
public class UserControllerTest {

    public static final String TEST_URL = "http://localhost:8080";

    public static final User testUser0 = new User(true, "test0", "first0", "last0", 0l, "record0", "filename0");
    public static final User testUser1 = new User(true, "test1", "first1", "last1", 1l, "record1", "filename1");

    UserControllerTestServiceApi serviceApi = new SecuredRestBuilder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
            .setClientId("mobile")
            .setUsername("admin")
            .setPassword("pass")
            .build()
            .create(UserControllerTestServiceApi.class);

    @Test
    public void addUserTest() {
        serviceApi.deleteAll();
        User user = serviceApi.addUser(testUser0);
        assertEquals(testUser0.getUsername(), user.getUsername());
        serviceApi.deleteAll();
    }

    @Test
    public void getUserTest() {
        serviceApi.deleteAll();
        serviceApi.addUser(testUser0);
        User user = serviceApi.getUser(testUser0.getUsername());
        assertEquals(testUser0.getUsername(), user.getUsername());
        serviceApi.deleteAll();
    }

    @Test
    public void deleteAllTest() {
        serviceApi.deleteAll();
        serviceApi.addUser(testUser0);
        serviceApi.deleteAll();
        User user = serviceApi.getUser(testUser0.getUsername());
        assertEquals(null, user);
    }
}
