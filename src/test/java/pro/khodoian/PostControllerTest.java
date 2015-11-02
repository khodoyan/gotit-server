package pro.khodoian;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.FollowerControllerTestServiceApi;
import pro.khodoian.client.PostControllerTestServiceApi;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.UserControllerTestServiceApi;
import pro.khodoian.models.*;
import retrofit.client.ApacheClient;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by eduardkhodoyan on 11/2/15.
 */
public class PostControllerTest {

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

    public static final Relation testRelation0 = new Relation(
            testUser1.getUsername(),
            testUser0.getUsername(),
            false, // isConfirmed
            true, // isFollowed
            true, // shareFeeling
            true, // shareBloodSugar
            true, // shareInsulin
            true  // shareQuestionnaire
    );

    public static final Relation testRelation1 = new Relation(
            testUser0.getUsername(),
            testUser1.getUsername(),
            false, // isConfirmed
            true, // isFollowed
            true, // shareFeeling
            true, // shareBloodSugar
            true, // shareInsulin
            true  // shareQuestionnaire
    );

    public static final Follower testFollower0 = Follower.makeFollower(testUser0.toUser(), testRelation0, false);
    public static final Follower testFollower1 = Follower.makeFollower(testUser1.toUser(), testRelation1, false);

    UserControllerTestServiceApi userServiceApi = new SecuredRestBuilder()
            .setClient(new ApacheClient())
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
            .setClientId("mobile")
            .setUsername("admin")
            .setPassword("pass")
            .build()
            .create(UserControllerTestServiceApi.class);


    private FollowerControllerTestServiceApi getFollowerServiceApi(String username, String password) {
        return new SecuredRestBuilder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)
                .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
                .setClientId("mobile")
                .setUsername(username)
                .setPassword(password)
                .build()
                .create(FollowerControllerTestServiceApi.class);
    }

    private PostControllerTestServiceApi getPostServiceApi(String username, String password) {
        return new SecuredRestBuilder()
                .setClient(new ApacheClient())
                .setEndpoint(TEST_URL)
                .setLoginEndpoint(TEST_URL + OAuth2Configuration.TOKEN_PATH)
                .setClientId("mobile")
                .setUsername(username)
                .setPassword(password)
                .build()
                .create(PostControllerTestServiceApi.class);
    }

    public static final Post[] posts = new Post[] {
            new Post(
                    testUser0.getUsername(), // this.username = username;
                    0, // this.updatedAt = updatedAt;
                    0, // this.deletedAt = deletedAt;
                    new java.util.Date().getTime(), // this.timestamp = timestamp;
                    true, // this.isShared = isShared;
                    Post.Feeling.GOOD, // this.feeling = feeling;
                    1f, // this.bloodSugar = bloodSugar;
                    true, // this.administeredInsulin = administeredInsulin;
                    null // this.questionnaire = questionnaire;
            ),
            new Post(
                    testUser1.getUsername(), // this.username = username;
                    0, // this.updatedAt = updatedAt;
                    0, // this.deletedAt = deletedAt;
                    new java.util.Date().getTime(), // this.timestamp = timestamp;
                    true, // this.isShared = isShared;
                    Post.Feeling.GOOD, // this.feeling = feeling;
                    2f, // this.bloodSugar = bloodSugar;
                    true, // this.administeredInsulin = administeredInsulin;
                    null // this.questionnaire = questionnaire;
            ),
            new Post(
                    testUser1.getUsername(), // this.username = username;
                    0, // this.updatedAt = updatedAt;
                    0, // this.deletedAt = deletedAt;
                    new java.util.Date().getTime(), // this.timestamp = timestamp;
                    true, // this.isShared = isShared;
                    Post.Feeling.GOOD, // this.feeling = feeling;
                    3f, // this.bloodSugar = bloodSugar;
                    true, // this.administeredInsulin = administeredInsulin;
                    null // this.questionnaire = questionnaire;
            )
    };


    @Before
    public void initiate() {
        PostControllerTestServiceApi postService = getPostServiceApi(
                admin.getUsername(),
                admin.getPassword()
        );
        postService.deleteAll();
        if(userServiceApi.getUser(testUser0.getUsername()) == null)
            userServiceApi.signup(testUser0);
        if(userServiceApi.getUser(testUser1.getUsername()) == null)
            userServiceApi.signup(testUser1);
        FollowerControllerTestServiceApi followerService = getFollowerServiceApi(
                testUser0.getUsername(), testUser0.getPassword()
        );
        followerService.add(testFollower1);
    }

    @After
    public void finish() {
        FollowerControllerTestServiceApi followerService = getFollowerServiceApi(
                testUser0.getUsername(), testUser0.getPassword()
        );
        followerService.delete(testUser1.getUsername());
        userServiceApi.delete(testUser0.getUsername());
        userServiceApi.delete(testUser1.getUsername());

        PostControllerTestServiceApi postService = getPostServiceApi(
                admin.getUsername(),
                admin.getPassword()
        );
        postService.deleteAll();
    }

    @Test
    public void addPostTest() {
        PostControllerTestServiceApi service0 = getPostServiceApi(testUser0.getUsername(), testUser0.getPassword());
        PostControllerTestServiceApi service1 = getPostServiceApi(testUser1.getUsername(), testUser1.getPassword());

        // publish post as user1 and check it is okay
        Post post = service1.add(posts[1]);
        assertPostNoId(posts[1], post);

        // get post as user1 and check it is okay
        ArrayList<Post> postsList;
        postsList = service1.getByUser(testUser1.getUsername());
        assertPostNoId(posts[1], postsList.get(0));

        // try to get post as user0. Check you do not get it. There is no confirmed connection
        //postsList = service0.getByUser(testUser1.getUsername());
        //assertEquals(0, postsList.size());

        // confirm relation, get post, check you got it
        FollowerControllerTestServiceApi followerService1 = getFollowerServiceApi(testUser1.getUsername(),
                testUser1.getPassword());
        followerService1.confirm(testUser0.getUsername(), new FollowSettings(
                true,
                true,
                true,
                true,
                true
        ));
        postsList = service0.getByUser(testUser1.getUsername());
        assertPostNoId(posts[1], postsList.get(0));
        long id = postsList.get(0).getId();
        service1.delete(id);
    }

    //@Test
    public void addPostsBulkTest() {

    }

    //@Test
    public void canReadPost() {

    }

    //@Test
    public void cantReadPost() {

    }

    private void assertPostNoId(Post expected, Post actual) {
        if (expected == null || actual == null)
            assertEquals(expected, actual);
        assertEquals(expected.getUsername(), actual.getUsername()); // this.username = username;
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt()); // this.updatedAt = updatedAt;
        assertEquals(expected.getDeletedAt(), actual.getDeletedAt()); // this.deletedAt = deletedAt;
        //assertEquals(expected.getTimestamp(), actual.getTimestamp()); // this.timestamp = timestamp;
        assertEquals(expected.getIsShared(), actual.getIsShared()); // this.isShared = isShared;
        assertEquals(expected.getFeeling(), actual.getFeeling()); // this.feeling = feeling;
        assertEquals(expected.getBloodSugar(), actual.getBloodSugar(),0.1f); // this.bloodSugar = bloodSugar;
        assertEquals(expected.getAdministeredInsulin(), actual.getAdministeredInsulin()); // this.administeredInsulin = administeredInsulin;
        assertEquals(expected.getQuestionnaire(), actual.getQuestionnaire()); // this.questionnaire = questionnaire;
    }

    private void assertPostWithId(Post expected, Post actual) {
        assertPostNoId(expected, actual);
        assertEquals(expected.getId(), actual.getId());
    }
}
