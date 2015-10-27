package pro.khodoian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pro.khodoian.client.SecuredRestBuilder;
import pro.khodoian.client.TestServiceApi;
import pro.khodoian.models.TestModel;
import retrofit.client.ApacheClient;

import static java.lang.System.out;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = GotitServerApplication.class)
public class GotitServerApplicationTests {

	TestServiceApi testServiceApi = new SecuredRestBuilder()
			.setClient(new ApacheClient())
			.setEndpoint("http://localhost:8080")
			.setLoginEndpoint("http://localhost:8080/oauth/token")
			.setClientId("mobile")
			.setUsername("admin")
			.setPassword("pass")
			.build()
			.create(TestServiceApi.class);

	@Test
	public void contextLoads() {
	}

	@Test
	public void OAuth2AccessAnonymousTest() {
		TestModel anonymous = testServiceApi.testAnonymous();
		assertEquals(anonymous.getResult(),"Ok");
		TestModel auth = testServiceApi.testAuth();
	}

	@Test
	public void OAuth2AccessAuthorizedTest() {
		TestModel auth = testServiceApi.testAuth();
		assertEquals(auth.getResult(),"Ok");
	}

}
