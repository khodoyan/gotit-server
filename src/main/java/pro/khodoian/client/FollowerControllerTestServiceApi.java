package pro.khodoian.client;

import pro.khodoian.clientmodels.Follower;
import pro.khodoian.controllers.FollowerController;
import retrofit.http.*;

import java.util.ArrayList;

/**
 * Retrofit interface designed for testing FollowerController
 *
 * @author eduardkhodoyan
 */
public interface FollowerControllerTestServiceApi {
    @POST(FollowerController.CONTROLLER_PATH)
    Void add(@Body Follower follower);

    @GET(FollowerController.CONTROLLER_PATH + "/{username}")
    Follower get(@Path("username") String username);

    @GET(FollowerController.CONTROLLER_PATH)
    ArrayList<Follower> getAll();

    @PUT(FollowerController.CONTROLLER_PATH)
    Void updateSettings(@Body Follower follower);

    @POST(FollowerController.CONTROLLER_PATH + "/confirm/{username}")
    Void confirm(@Path("username") String username);

    @DELETE(FollowerController.CONTROLLER_PATH + "/{username}")
    Follower delete(@Path("username") String username);

    @DELETE(FollowerController.CONTROLLER_PATH + "/delete_all")
    Void deleteAll();
}
