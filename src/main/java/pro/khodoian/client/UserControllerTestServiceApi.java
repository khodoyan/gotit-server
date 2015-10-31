package pro.khodoian.client;

import pro.khodoian.controllers.UserController;
import pro.khodoian.models.SignupUser;
import pro.khodoian.models.TestModel;
import pro.khodoian.models.User;
import retrofit.http.*;

/**
 * Retrofit interface designed for testing UserController
 *
 * @author eduardkhodoyan
 */
public interface UserControllerTestServiceApi {

    @GET(UserController.CONTROLLER_PATH + "/{username}")
    public User getUser(@Path("username") String username);

    @GET("/test_get_username")
    public TestModel getUsername();

    @POST(UserController.CONTROLLER_PATH + "/signup")
    public Void signup(@Body SignupUser user);

    /**
     * Changes password for user who calls it
     *
     * @param passwords array of two string[] {oldPassword, newPassword}
     * @return void
     */
    @PUT(UserController.CONTROLLER_PATH + "/change_password")
    public Void changePassword(@Body String[] passwords);

    @DELETE(UserController.CONTROLLER_PATH + "/{username}")
    public Void delete(@Path("username") String username);
}
