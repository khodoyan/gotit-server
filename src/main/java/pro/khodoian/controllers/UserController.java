package pro.khodoian.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.models.Authority;
import pro.khodoian.models.SignupUser;
import pro.khodoian.models.User;
import pro.khodoian.models.UserDetailsImpl;
import pro.khodoian.services.UserRepository;

/**
 * Controller class that manages User (for testing purposes)
 */
@Controller
public class UserController {
    // TODO: make this class not available to people of delete it

    public static final String CONTROLLER_PATH = "/user";

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsManager userDetailsManager;

    @RequestMapping(value = CONTROLLER_PATH + "/{username}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        try {
            String principal = OAuth2Configuration.getPrincipal();
            UserDetails userDetails = null;
            UserDetailsImpl userDetails1 = null;
            if (principal != null) {
                userDetails = userDetailsManager.loadUserByUsername(principal);
                userDetails1 = UserDetailsImpl.makeUserDetailsImpl(userDetails);
            }

            // check request
            if (username == null || username.equals("")
                    || userDetails == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (!userDetailsManager.userExists(username)) {
                User result = null;
                return new ResponseEntity<>(result, HttpStatus.OK);
            }

            // check authorisation
            boolean isAdmin = (userDetails1 != null && userDetails1.hasAuthority(Authority.ADMIN));
            if (!username.equals(principal) && !isAdmin)
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            User result = userRepository.findOne(username);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/signup", method = RequestMethod.POST)
    public ResponseEntity<Void> signup(@RequestBody SignupUser signupUser) {
        // validate data
        if (!signupUser.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // check if exists
        if (userRepository.exists(signupUser.getUsername()) || userDetailsManager.userExists(signupUser.getUsername()))
            return new ResponseEntity<>(HttpStatus.CONFLICT); // TODO: check the code

        // get User instance and save credentials and User to tables
        try {
            User user = signupUser.toUser();
            if(userRepository.save(user) == null)
                throw new Exception("Error adding User to repository");
            UserDetailsImpl userDetails = UserDetailsImpl.makeUserDetailsImpl(signupUser);
            userDetailsManager.createUser(userDetails);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/change_password", method = RequestMethod.PUT)
    public ResponseEntity<Void> changePassword(@RequestBody String[] strings) {
        // TODO: Understand why not working properly
        // get principal
        String principal = OAuth2Configuration.getPrincipal();
        if (principal == null || principal.equals(""))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // validate password
        if (strings == null || strings.length < 2
                || strings[1] == null || strings[1].length() < SignupUser.MIN_PASSWORD_LENGTH)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // get User current user password and update password
        try {
            //UserDetailsImpl user = (UserDetailsImpl) userDetailsManager.loadUserByUsername(principal);
            //if (user == null)
            //    throw new Exception("User can't be received from repository");
            userDetailsManager.changePassword(strings[0], strings[1]);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("username") String username) {
        // TODO: Understand why not working properly
        try {
            // validate input and check if user exists
            if (username == null || username.trim().equals("") || !userDetailsManager.userExists(username.trim()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // check rights
            UserDetailsImpl userDetails = null;
            if (userDetailsManager.userExists(username))
            userDetails = UserDetailsImpl.makeUserDetailsImpl(
                    userDetailsManager.loadUserByUsername(username));
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals("") ||
                    (!principal.equals(username)
                            && userDetails != null && userDetails.hasAuthority(Authority.ADMIN))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // actually delete user and return 200
            if (userDetailsManager.userExists(username))
                userDetailsManager.deleteUser(username);
            if (userRepository.exists(username))
                userRepository.delete(username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
