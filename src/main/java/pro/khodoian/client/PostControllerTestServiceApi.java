package pro.khodoian.client;

import pro.khodoian.controllers.PostController;
import pro.khodoian.models.BloodSugar;
import pro.khodoian.models.Post;
import retrofit.http.*;

import java.util.ArrayList;

/**
 * Retrofit interface designed for testing PostController
 *
 * @author eduardkhodoyan
 */
public interface PostControllerTestServiceApi {
    @POST(value = PostController.CONTROLLER_PATH)
    public Post add(@Body Post post);

    @POST(value = PostController.CONTROLLER_PATH + "/bulk")
    public ArrayList<Post> addBulk(@Body ArrayList<Post> posts);

    @GET(value = PostController.CONTROLLER_PATH + "/all")
    public ArrayList<Post> getAll();

    @GET(value = PostController.CONTROLLER_PATH + "/user/{username}")
    public ArrayList<Post> getByUser(@Path("username") String username);

    @GET(value = PostController.CONTROLLER_PATH + "/insulin/{username}")
    public ArrayList<BloodSugar> getInsulinByUser(@Path("username") String username);

    @DELETE(value = PostController.CONTROLLER_PATH + "/{id}")
    public Void delete(@Path("id") long id);

    @DELETE(value = PostController.CONTROLLER_PATH + "/delete_all")
    public Void deleteAll();

}
