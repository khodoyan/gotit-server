package pro.khodoian.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.khodoian.models.TestModel;
import pro.khodoian.services.UserService;

/**
 * Hello world class for checking Spring Boot app
 *
 * @author eduardkhodoyan
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @RequestMapping("/testanonymous")
    @ResponseBody
    public TestModel home() {
        return new TestModel();
    }

    @RequestMapping("/testauth")
    @ResponseBody
    public TestModel testAuth() {
        return new TestModel();
    }

}
