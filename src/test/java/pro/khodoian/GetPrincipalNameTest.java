package pro.khodoian;

import org.junit.Test;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.UserControllerTestServiceApi;
import pro.khodoian.models.TestModel;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class for testing getting username of logged in user
 * Shall be run, when Application is running in background
 *
 * @author eduardkhodoyan
 */
public class GetPrincipalNameTest {
    public static final String TEST_URL = Shared.TEST_URL;

    @Test
    public void getAuthenticatedUsername() {
        UserControllerTestServiceApi authorizedServiceApi = new SecuredRestBuilder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)
                .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
                .setClientId("mobile")
                .setUsername("admin")
                .setPassword("pass")
                .build()
                .create(UserControllerTestServiceApi.class);
        TestModel test = authorizedServiceApi.getUsername();
        assertEquals("admin", test.getResult());
    }

    @Test
    public void getUnauthenticatedUsername() {
        UserControllerTestServiceApi unauthorizedApi = new RestAdapter.Builder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)

                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(UserControllerTestServiceApi.class);

        TestModel test = unauthorizedApi.getUsername();
        assertEquals(null, test.getResult());
    }
}
