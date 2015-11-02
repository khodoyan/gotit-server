package pro.khodoian;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.FollowerControllerTestServiceApi;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.UserControllerTestServiceApi;
import pro.khodoian.models.*;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

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

    public static final SignupUser testUser0 = SignupUser.makeUser(
            "user0",
            "password0",
            true,
            "user0_firstname",
            "user0_lastname",
            1,
            "user0_medical1",
            "user0_userpic1",
            SignupUser.Role.PATIENT);

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

    public static final SignupUser testUser2 = SignupUser.makeUser(
            "user2",
            "password2",
            true,
            "user2_firstname",
            "user2_lastname",
            1,
            "user2_medical1",
            "user2_userpic1",
            SignupUser.Role.FOLLOWER);

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

    public static final Follower testFollower1 = Follower.makeFollower(testUser1.toUser(), testRelation0, false);
    public static final Follower testFollower2 = Follower.makeFollower(testUser2.toUser(), testRelation1, false);

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
            .setUsername(testUser1.getUsername())
            .setPassword(testUser1.getPassword())
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
//        userServiceApi.signup(admin);
        User test = userServiceApi.getUser(testUser1.getUsername());
        if(userServiceApi.getUser(testUser0.getUsername()) == null)
            userServiceApi.signup(testUser0);
        if(userServiceApi.getUser(testUser1.getUsername()) == null)
            userServiceApi.signup(testUser1);
        if(userServiceApi.getUser(testUser2.getUsername()) == null)
            userServiceApi.signup(testUser2);
        adminServiceApi.deleteAll();
    }

    @After
    public void finish() {
        adminServiceApi.deleteAll();
        userServiceApi.delete(testUser0.getUsername());
        //if(userServiceApi.getUser(testUser1.getUsername()) != null)
        userServiceApi.delete(testUser1.getUsername());
        //if(userServiceApi.getUser(testUser2.getUsername()) != null)
        userServiceApi.delete(testUser2.getUsername());
    }

    @Test
    public void addAndGetTest() {
        adminServiceApi.add(testFollower1);
        Follower actual = adminServiceApi.get(testUser1.getUsername());
        assertFollower(testFollower1, actual);
    }

    @Test
    public void getAllTest() {
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
        adminServiceApi.add(testFollower1);
        adminServiceApi.delete(testUser1.getUsername());
        assertFollower(null, adminServiceApi.get(testUser1.getUsername()));
    }

    //@Test
    public void updateTest() {
        /*
        adminServiceApi.deleteAll();
        adminServiceApi.add(testFollower1);
        Follower updatedFollower = Follower.makeFollower(testUser1.toUser(), updatedTestRelation0);
        adminServiceApi.updateSettings(updatedFollower);
        Follower actual = adminServiceApi.get(testUser1.getUsername());
        assertFollower(updatedFollower, actual);
        */
    }

    @Test
    public void confirmTest() {
        adminServiceApi.add(testFollower1);
        Follower expected = Follower.makeFollower(testUser1.toUser(), testRelation0, true);
        user1ServiceApi.confirm(admin.getUsername(), new FollowSettings(
                true,
                true,
                true,
                true,
                true
        ));
        Follower actual = adminServiceApi.get(testUser1.getUsername());
        assertFollower(expected, actual);
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
