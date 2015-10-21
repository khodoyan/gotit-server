package pro.khodoian.controllers;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Hello world class for checking Spring Boot app
 *
 * @author eduardkhodoyan 
 */
@Controller
@EnableAutoConfiguration
public class home {

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "This is my first working Spring Boot application made from scratch";
    }

}
