package pro.khodoian.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.client.OAuth2TestServiceApi;
import pro.khodoian.models.TestModel;

/**
 * Hello world class for checking Spring Boot app
 *
 * @author eduardkhodoyan
 */
@Controller
public class TestController {

    @RequestMapping(OAuth2TestServiceApi.TEST_ANONYMOUS)
    @ResponseBody
    public TestModel testAnonymous() {
        return new TestModel();
    }

    @RequestMapping(OAuth2TestServiceApi.TEST_AUTHORIZED)
    @ResponseBody
    public TestModel testAuth() {
        return new TestModel();
    }

    @RequestMapping("/test_get_username")
    @ResponseBody
    public TestModel getUsername() {
        return new TestModel(OAuth2Configuration.getUsername());
    }

}
