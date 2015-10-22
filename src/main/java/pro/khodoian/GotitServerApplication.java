package pro.khodoian;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class GotitServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GotitServerApplication.class, args);
    }

}