package pro.khodoian.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pro.khodoian.models.User;
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

    @RequestMapping(value = CONTROLLER_PATH, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            // check argument
            if (user == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // make sure user not exists
            if (userRepository.exists(user.getUsername()))
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

            // add new user
            return new ResponseEntity<>(userRepository.save(user),HttpStatus.OK);
        } catch (Exception e) {
            // something went wrong
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/{username}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        try {
            if (username == null || username.equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(userRepository.findOne(username),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/delete/{username}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) {
        try {
            if (username == null || username.equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            userRepository.delete(username);
            if (userRepository.findOne(username) == null)
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/delete_all")
    public ResponseEntity<Void> deleteAll() {
        try {
            userRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
