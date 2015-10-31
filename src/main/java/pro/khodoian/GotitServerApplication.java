package pro.khodoian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pro.khodoian.auth.OAuth2Configuration;

/**
 * Class used to start Spring application
 *
 * @author eduardkhodoyan
 */
@SpringBootApplication
@ComponentScan
@EnableJpaRepositories
@Import(OAuth2Configuration.class)
public class GotitServerApplication {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(GotitServerApplication.class, args);
    }
}