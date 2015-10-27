package pro.khodoian.client;

import pro.khodoian.models.TestModel;
import retrofit.http.GET;

/**
 * Retrofit interface designed for testing authorization
 *
 * @author eduardkhodoyan
 */
public interface TestServiceApi {

    String TEST_ANONYMOUS = "/testanonymous";
    String TEST_AUTHORIZED = "/testauth";


    @GET(TEST_ANONYMOUS)
    public TestModel testAnonymous();

    @GET(TEST_AUTHORIZED)
    public TestModel testAuth();
}
