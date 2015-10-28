package pro.khodoian.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.khodoian.clientmodels.ClientUser;

import java.util.ArrayList;

/**
 * Controller class that manages people and provides followers and following people to authenticated user
 *
 * @author eduardkhodoyan
 */
public class PeopleController {
    /*
    // TODO: Implement class

    public static final String PATH_CONTROLLER = "/people";

    @RequestMapping(value = PATH_CONTROLLER)
    @ResponseBody
    public ResponseEntity<ArrayList<ClientUser>> getAll() {
        ArrayList<ClientUser> followers = new ArrayList<>();
        ArrayList<ClientUser> following = new ArrayList<>();

    }

    @RequestMapping(value = PATH_CONTROLLER, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ClientUser> add(@RequestBody ClientUser user) {

    }

    @RequestMapping(value = PATH_CONTROLLER + "/{username}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ClientUser> update(@PathVariable("username") String username, @RequestBody ClientUser user) {
        // check data validity
        if (username == null || username.equals("") || user == null
                || user.getUsername() == null || user.getUsername().equals("")
                || username != user.getUsername())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // TODO: update the record in database
    }

    @RequestMapping(value = PATH_CONTROLLER + "/{username}", method = RequestMethod.DELETE)
    private ResponseEntity<Void> delete(@PathVariable("username") String username) {

    }
    */
}
