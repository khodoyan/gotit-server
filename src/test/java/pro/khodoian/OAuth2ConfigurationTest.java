package pro.khodoian;

import org.apache.http.HttpStatus;
import org.junit.Test;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.SecuredRestException;
import pro.khodoian.client.OAuth2TestServiceApi;
import pro.khodoian.models.TestModel;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class for testing security configuration.
 * Shall be run, when Application is running in background
 *
 * @author eduardkhodoyan
 */

public class OAuth2ConfigurationTest {
    public static final String TEST_URL = "http://localhost:8080";

    private class ErrorRecorder implements ErrorHandler {
        private RetrofitError error;

        @Override
        public Throwable handleError(RetrofitError cause) {
            error = cause;
            return error.getCause();
        }

        public RetrofitError getError() {
            return error;
        }
    }

    OAuth2TestServiceApi OAuth2TestServiceApi = new SecuredRestBuilder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
            .setClientId("mobile")
            .setUsername("admin")
            .setPassword("pass")
            .build()
            .create(OAuth2TestServiceApi.class);

    @Test
    public void oauth2AccessAnonymousTest() {
        TestModel anonymous = OAuth2TestServiceApi.testAnonymous();
        assertEquals(anonymous.getResult(),"Ok");
    }

    @Test
    public void oauth2AccessAuthorizedTest() {
        TestModel auth = OAuth2TestServiceApi.testAuth();
        assertEquals(auth.getResult(),"Ok");
    }

    @Test(expected = SecuredRestException.class)
    public void denyAccessBadCredentials() {
        ErrorRecorder errorRecorder = new ErrorRecorder();
        OAuth2TestServiceApi unauthorizedApi = new SecuredRestBuilder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)
                .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
                .setClientId("mobile")
                .setUsername("incorrect_username")
                .setPassword("incorrect_password")
                .setErrorHandler(errorRecorder)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(OAuth2TestServiceApi.class);
        unauthorizedApi.testAuth();
        fail("Should have received unauthorized error");
    }

    @Test
    public void denyAccessToAuthorizedResource() {
        ErrorRecorder errorRecorder = new ErrorRecorder();
        OAuth2TestServiceApi unauthorizedApi = new RestAdapter.Builder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)
                .setErrorHandler(errorRecorder)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(OAuth2TestServiceApi.class);
        try {
            unauthorizedApi.testAuth();
            fail("Should have received unauthorized error");
        } catch (Exception e) {
            assertEquals(HttpStatus.SC_UNAUTHORIZED, errorRecorder.getError().getResponse().getStatus());
        }
    }
}
