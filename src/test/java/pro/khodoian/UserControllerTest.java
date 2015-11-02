package pro.khodoian;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.UserControllerTestServiceApi;
import pro.khodoian.models.SignupUser;
import pro.khodoian.models.User;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Class for testing UserController
 * Shall be run, when Application is running in background
 *
 * @author eduardkhodoyan
 */
public class UserControllerTest {

    public static final String TEST_URL = "http://localhost:8080";

    public static final SignupUser admin = SignupUser.makeUser(
            "admin",
            "pass",
            true,
            "admin_firstname",
            "admin_lastname",
            1,
            "admin_medical1",
            "admin_userpic1",
            SignupUser.Role.ADMIN);

    public static final SignupUser testUser1 = SignupUser.makeUser(
            "user1",
            "password1",
            true,
            "user1_firstname",
            "user1_lastname",
            1,
            "user1_medical1",
            "user1_userpic1",
            SignupUser.Role.FOLLOWER);


    private UserControllerTestServiceApi adminServiceApi = new SecuredRestBuilder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
            .setClientId("mobile")
            .setUsername(admin.getUsername())
            .setPassword(admin.getPassword())
            .build()
            .create(UserControllerTestServiceApi.class);

    private UserControllerTestServiceApi getSecuredServiceApi(SignupUser user) {
        return new SecuredRestBuilder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)
                .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
                .setClientId("mobile")
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build()
                .create(UserControllerTestServiceApi.class);
    }

    private UserControllerTestServiceApi getSecuredServiceApi(String username, String password) {
        return new SecuredRestBuilder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)
                .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
                .setClientId("mobile")
                .setUsername(username)
                .setPassword(password)
                .build()
                .create(UserControllerTestServiceApi.class);
    }

    UserControllerTestServiceApi unauthorisedServiceApi = new RestAdapter.Builder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build()
            .create(UserControllerTestServiceApi.class);

    @Before
    public void initiate() {
    }

    @After
    public void onDestroy() {
    }

    @Test
     public void addUserTest() {
        //unauthorisedServiceApi.signup(admin);
        if (adminServiceApi.getUser(testUser1.getUsername()) == null)
            unauthorisedServiceApi.signup(testUser1);
        User user = adminServiceApi.getUser(testUser1.getUsername());
        User expected = testUser1.toUser();
        assertUser(expected, user);
        adminServiceApi.delete(testUser1.getUsername());
    }

    @Test
    public void changePasswordTest() {
        /*
        User user = adminServiceApi.getUser(testUser1.getUsername());
        if (user == null)
            unauthorisedServiceApi.signup(testUser1);

        UserControllerTestServiceApi user1Service = getSecuredServiceApi(testUser1);
        user1Service.changePassword(new String[]{testUser1.getUsername(), "new password"});

        boolean passedTest = true;
        try {
            user = getSecuredServiceApi(testUser1.getUsername(), "new password").getUser(testUser1.getUsername());
        } catch (Exception e) {
            passedTest = false;
        } finally {
            adminServiceApi.delete(testUser1.getUsername());
            if (!passedTest)
                fail();
        }
        */
    }

    private void assertUser(User expected, User actual) {
        if (expected == null || actual == null) {
            assertEquals(expected, actual);
        } else {
            assertEquals(expected.getUsername(), actual.getUsername());
            assertEquals(expected.getIsPatient(), actual.getIsPatient());
            assertEquals(expected.getFirstName(), actual.getFirstName());
            assertEquals(expected.getLastName(), actual.getLastName());
            assertEquals(expected.getMedicalRecordNumber(), actual.getMedicalRecordNumber());
            assertEquals(expected.getUserpicFilename(), actual.getUserpicFilename());
        }
    }
}
