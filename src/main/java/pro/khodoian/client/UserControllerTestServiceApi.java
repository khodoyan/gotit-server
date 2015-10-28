package pro.khodoian.client;

import pro.khodoian.controllers.TestController;
import pro.khodoian.controllers.UserController;
import pro.khodoian.models.TestModel;
import pro.khodoian.models.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Retrofit interface designed for testing authorization
 *
 * @author eduardkhodoyan
 */
public interface UserControllerTestServiceApi {

    @POST(UserController.PATH_CONTROLLER)
    User addUser(@Body User user);

    @GET(UserController.PATH_CONTROLLER + "/{username}")
    public User getUser(@Path("username") String username);

    @GET(UserController.PATH_CONTROLLER + "/delete_all")
    public Void deleteAll();

    @GET("/test_get_username")
    public TestModel getUsername();
}
