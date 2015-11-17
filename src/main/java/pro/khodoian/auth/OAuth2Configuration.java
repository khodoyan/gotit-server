package pro.khodoian.auth;

/**
 * OAuth2 security configuration file
 *
 * @author eduardkhodoyan
 */

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.RestController;
import pro.khodoian.client.OAuth2TestServiceApi;
import pro.khodoian.controllers.UserController;

@Configuration
@EnableAutoConfiguration
@RestController
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class OAuth2Configuration {

    public static final String TOKEN_PATH = "/oauth/token";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsManager userDetailsManager() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        return manager;
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources)
                throws Exception {
            resources.tokenStore(tokenStore);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers(TOKEN_PATH).permitAll()
                    .antMatchers(OAuth2TestServiceApi.TEST_ANONYMOUS).permitAll()
                    .antMatchers("/test_get_username").permitAll()
                    .antMatchers(HttpMethod.POST, UserController.CONTROLLER_PATH + "/signup").permitAll()
                    .antMatchers("/**").authenticated()
            ;
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager auth;

        @Autowired
        private DataSource dataSource;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Bean
        public JdbcTokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }

        @Bean
        protected AuthorizationCodeServices authorizationCodeServices() {
            return new JdbcAuthorizationCodeServices(dataSource);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security)
                throws Exception {
            // TODO: turn on passwords hashing
            security.passwordEncoder(passwordEncoder);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {
            endpoints
                    .authenticationManager(auth)
                    .authorizationCodeServices(authorizationCodeServices())
                    .tokenStore(tokenStore())
                    .approvalStoreDisabled();
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("mobile")
                    .authorizedGrantTypes("password")
                    .authorities("ROLE_CLIENT")
                    .scopes("read")
                    .resourceIds("oauth2-resource")
                    .accessTokenValiditySeconds(0)
            ;
        }

    }

    @Autowired
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                //.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                //.withUser("user1")
                //.password("pass")
                //.roles("PATIENT", "FOLLOWER")
        ;
    }

    /**
     * Returns the name of authenticated user
     * @return String representing username of logged in user or null if no users login
     */
    public static String getPrincipal() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String
                && ((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .equals("anonymousUser")) {
            return null;
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null)
            return null;
        else
            return userDetails.getUsername();
    }
}
