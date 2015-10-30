package pro.khodoian;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.FollowerControllerTestServiceApi;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.UserControllerTestServiceApi;
import pro.khodoian.clientmodels.Follower;
import pro.khodoian.models.Relation;
import pro.khodoian.models.User;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class for testing FollowerController
 * Shall be run, when Application is running in background
 *
 * @author eduardkhodoyan
 */
public class FollowerControllerTest {

    public class ErrorRecorder implements ErrorHandler {
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

    ErrorRecorder errorRecorder = new ErrorRecorder();

    public static final String TEST_URL = "http://localhost:8080";

    public static final User testUser0 = new User(true, "admin", "firstname0", "lastname0",0,"record0","userpic0");
    public static final User testUser1 = new User(true, "user1", "firstname1", "lastname1",0,"record1","userpic1");
    public static final User testUser2 = new User(true, "user2", "firstname2", "lastname2",0,"record2","userpic2");

    public static final Relation testRelation0 = new Relation(
            testUser0.getUsername(),
            testUser1.getUsername(),
            true, // isConfirmed
            true, // isFollowed
            true, // shareFeeling
            true, // shareBloodSugar
            true, // shareInsulin
            true  // shareQuestionnaire
    );

    public static final Relation updatedTestRelation0 = new Relation(
            testUser0.getUsername(),
            testUser1.getUsername(),
            true, // isConfirmed
            false, // isFollowed
            false, // shareFeeling
            false, // shareBloodSugar
            false, // shareInsulin
            false  // shareQuestionnaire
    );

    public static final Relation testRelation1 = new Relation(
            testUser0.getUsername(),
            testUser2.getUsername(),
            true, // isConfirmed
            true, // isShared
            true, // shareFeeling
            true, // shareBloodSugar
            true, // shareInsulin
            true  // shareQuestionnaire
    );

    /*
    public static final Relation testRelation2 = new Relation(
            testUser1.getUsername(),
            testUser0.getUsername(),
            true, // isConfirmed
            true, // isShared
            true, // shareFeeling
            true, // shareBloodSugar
            true, // shareInsulin
            true  // shareQuestionnaire
    );
    */


    public static final Follower testFollower1 = Follower.makeFollower(testUser1, testRelation0, false);
    public static final Follower testFollower2 = Follower.makeFollower(testUser2, testRelation1, false);

    FollowerControllerTestServiceApi adminServiceApi = new SecuredRestBuilder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
            .setClientId("mobile")
            .setUsername("admin")
            .setPassword("pass")
            .setErrorHandler(errorRecorder)
            .build()
            .create(FollowerControllerTestServiceApi.class);

    FollowerControllerTestServiceApi user1ServiceApi = new SecuredRestBuilder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
            .setClientId("mobile")
            .setUsername("user1")
            .setPassword("pass")
            .setErrorHandler(errorRecorder)
            .build()
            .create(FollowerControllerTestServiceApi.class);

    UserControllerTestServiceApi userServiceApi = new SecuredRestBuilder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
            .setClientId("mobile")
            .setUsername("admin")
            .setPassword("pass")
            .build()
            .create(UserControllerTestServiceApi.class);

    @Before
    public void initiate() {
        userServiceApi.addUser(testUser0);
        userServiceApi.addUser(testUser1);
        userServiceApi.addUser(testUser2);
    }

    @After
    public void finish() {
        userServiceApi.deleteAll();
        adminServiceApi.deleteAll();
    }

    @Test
    public void addAndGetTest() {
        adminServiceApi.deleteAll();
        adminServiceApi.add(testFollower1);
        Follower actual = adminServiceApi.get(testUser1.getUsername());
        assertFollower(testFollower1, actual);
    }

    @Test
    public void getAllTest() {
        adminServiceApi.deleteAll();
        adminServiceApi.add(testFollower1);
        adminServiceApi.add(testFollower2);
        ArrayList<Follower> list = adminServiceApi.getAll();
        Follower actual = list.get(0);
        assertFollower(testFollower1, actual);
        actual = list.get(1);
        assertFollower(testFollower2, actual);
    }

    @Test
    public void deleteTest() {
        adminServiceApi.deleteAll();
        adminServiceApi.add(testFollower1);
        adminServiceApi.delete(testUser1.getUsername());
        try {
            assertFollower(null, adminServiceApi.get(testUser1.getUsername()));
            fail("Test should have not passed here. 404 error should have been called");
        } catch (Exception e) {
            assertEquals(HttpStatus.SC_NOT_FOUND, errorRecorder.getError().getResponse().getStatus());
        }
    }

    @Test
    public void updateTest() {
        adminServiceApi.deleteAll();
        adminServiceApi.add(testFollower1);
        Follower updatedFollower = Follower.makeFollower(testUser1, updatedTestRelation0);
        adminServiceApi.updateSettings(updatedFollower);
        Follower actual = adminServiceApi.get(testUser1.getUsername());
        assertFollower(updatedFollower, actual);
    }

    @Test
    public void confirmTest() {
        adminServiceApi.deleteAll();
        adminServiceApi.add(testFollower1);
        Follower confirmedFollower = Follower.makeFollower(testUser1, testRelation0, true);
        user1ServiceApi.confirm(testUser0.getUsername());
        Follower actual = adminServiceApi.get(testUser1.getUsername());
        assertFollower(confirmedFollower, actual);
    }

    public void assertFollower(Follower expected, Follower actual) {
        if (expected == null || actual == null) {
            assertEquals(expected, actual);
        } else {
            System.out.println("Follower: " + actual.getFollower());
            assertEquals(expected.getFollower(), actual.getFollower());
            System.out.println("Firstname: " + actual.getFirstName());
            assertEquals(expected.getFirstName(), actual.getFirstName());
            System.out.println("Lastname: " + actual.getLastName());
            assertEquals(expected.getLastName(), actual.getLastName());
            System.out.println("UserpicFilename: " + actual.getUserpicFilename());
            assertEquals(expected.getUserpicFilename(), actual.getUserpicFilename());
            System.out.println("IsConfirmedByFollower: " + actual.isConfirmedByFollower());
            assertEquals(expected.isConfirmedByFollower(), actual.isConfirmedByFollower());
            System.out.println("IsConfirmedByPatient: " + actual.isConfirmedByPatient());
            assertEquals("isConfirmedByPatient doesn't match",
                    expected.isConfirmedByPatient(), actual.isConfirmedByPatient());
            System.out.println("IsFollowed: " + actual.isFollowed());
            assertEquals(expected.isFollowed(), actual.isFollowed());
            System.out.println("IsShareFeeling: " + actual.isShareFeeling());
            assertEquals(expected.isShareFeeling(), actual.isShareFeeling());
            System.out.println("IsShareBloodSugar: " + actual.isShareBloodSugar());
            assertEquals(expected.isShareBloodSugar(), actual.isShareBloodSugar());
            System.out.println("IsShareInsulin: " + actual.isShareInsulin());
            assertEquals(expected.isShareInsulin(), actual.isShareInsulin());
            System.out.println("IsShareQuestions: " + actual.isShareQuestions());
            assertEquals(expected.isShareQuestions(), actual.isShareQuestions());
        }
    }
}
