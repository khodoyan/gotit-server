package pro.khodoian.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.models.BloodSugar;
import pro.khodoian.models.Post;
import pro.khodoian.models.Relation;
import pro.khodoian.models.User;
import pro.khodoian.services.PostRepository;
import pro.khodoian.services.RelationRepository;
import pro.khodoian.services.UserRepository;
import org.springframework.data.domain.Page;

import java.util.ArrayList;

/**
 * Controller class that manages User (for testing purposes)
 */
@Controller
public class PostController {
    public static final String CONTROLLER_PATH = "/post";
    public static final int PAGE_SIZE = 50;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RelationRepository relationRepository;

    @RequestMapping(value = CONTROLLER_PATH, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Post> add(@RequestBody Post post) {
        try {
            // validate data
            if (post == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");
            User user = userRepository.findOne(principal);
            if (user == null || !user.getIsPatient())
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            post.setUsername(principal);

            // set timestamp to current UTC time
            post.setTimestamp(new java.util.Date().getTime());

            return new ResponseEntity<>(postRepository.save(post), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/bulk", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ArrayList<Post>> addBulk(@RequestBody ArrayList<Post> posts) {
        try {
            // validate data
            if (posts == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // get principal and set it to post, set timestamp in current UTC time
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");
            User user = userRepository.findOne(principal);
            if (user == null || !user.getIsPatient())
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            for (Post post : posts) {
                post.setUsername(principal);
                post.setTimestamp(new java.util.Date().getTime());
            }

            // add data and return result
            return new ResponseEntity<>(postRepository.save(posts), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/all", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Post>> getAll() {
        // TODO: check and possibly redo
        try {
            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");

            // check if user exists
            if (!userRepository.exists(principal))
                throw new Exception("Principal does not exist in the database");

            // get data, check permissions, and return result
            ArrayList<Post> result = new ArrayList<>();
            ArrayList<Post> rawPosts = postRepository.findByRelationFollowerAndRelationIsConfirmed(
                    new Sort(Sort.Direction.DESC, "timestamp"),
                    principal,
                    true);
            if (rawPosts != null) {
                for (Post post : rawPosts) {
                    Post checkedPost = post.checkShared().checkAuthorities(principal, relationRepository);
                    if (checkedPost != null)
                        result.add(checkedPost);
                }
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/user/{username}")
    public ResponseEntity<ArrayList<Post>> getByUser(@PathVariable("username") String username) {
        try {
            // check username
            if (username == null || username.equals("") || !userRepository.exists(username))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");

            // check if user exists
            if (!userRepository.exists(principal))
                throw new Exception("Principal does not exist in the database");

            // get relation and check if relation is confirmed

            ArrayList<Post> rawPosts = new ArrayList<>();
            if (username.equals(principal))
                rawPosts = postRepository.findByUsername(new Sort(Sort.Direction.DESC, "timestamp"), username);
            else {
                Relation relation = relationRepository.findOneByPatientAndFollower(
                        username,
                        principal);
                if (relation != null && relation.isConfirmed()) {
                    rawPosts = postRepository.findByUsername(username);
                }
            }
            return new ResponseEntity<>(checkAuthorities(rawPosts, principal), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/insulin/{username}")
    public ResponseEntity<ArrayList<BloodSugar>> getInsulinByUser(@PathVariable("username") String username) {
        // TODO: check and possibly redo
        try {
            // check username
            if (username == null || username.equals("") || !userRepository.exists(username))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");

            // check if user exists
            if (!userRepository.exists(principal))
                throw new Exception("Principal does not exist in the database");

            // check authorization
            Relation relation = relationRepository.findOneByPatientAndFollower(username, principal);
            if (!username.equals(principal) && (relation == null || !relation.isShareInsulin()
                    || !relation.isConfirmed()))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            // get data, check permissions, and return result
            ArrayList<BloodSugar> result = new ArrayList<>();
            ArrayList<Post> rawPosts = postRepository.findByUsername(
                    new Sort(Sort.Direction.DESC, "timestamp"),
                    username
            );
            if (rawPosts != null) {
                for (Post post : rawPosts) {
                    Post checkedPost = post.checkShared().checkAuthorities(principal, relationRepository);
                    if (checkedPost != null)
                        result.add(new BloodSugar(post.getTimestamp(), post.getBloodSugar()));
                }
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/page/{page}", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Post>> getPage(@PathVariable("page") int page) {
        // TODO: check and possibly redo
        try {
            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");

            // check if user exists
            if (!userRepository.exists(principal))
                throw new Exception("Principal does not exist in the database");

            // get data and return result
            PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, new Sort(Sort.Direction.DESC, "timestamp"));
            Page<Post> pageResult = postRepository.findByRelationFollowerAndRelationIsConfirmed(
                    pageRequest, principal, true);
            ArrayList<Post> result = new ArrayList<>();
            for (Post post : pageResult) {
                if (post != null) {
                    Post checkedPost = post.checkShared().checkAuthorities(principal, relationRepository);
                    if (checkedPost != null)
                        result.add(checkedPost);
                }
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        // TODO: check and possibly redo
        try {
            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");

            // check if user exists
            if (!userRepository.exists(principal))
                throw new Exception("Principal does not exist in the database");

            // check if post exists
            if (!postRepository.exists(id))
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // check if have authorization
            Post post = postRepository.findOne(id);
            if (post == null || !principal.equals(post.getUsername()))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            // delete item, check if deleted and return result
            postRepository.delete(id);
            if (postRepository.exists(id))
                throw new Exception("Can't delete post");
            else
                return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ArrayList<Post> checkAuthorities(Iterable<Post> posts, String follower) {
        if (posts == null)
            return null;

        ArrayList<Post> result = new ArrayList<>();
        if (posts != null) {
            for (Post post : posts) {
                Post checkedPost = null;
                if (post != null)
                    checkedPost = post.checkShared();
                if (checkedPost != null)
                        checkedPost = checkedPost.checkAuthorities(follower, relationRepository);
                if (checkedPost != null)
                    result.add(checkedPost);
            }
        }
        return result;
    }


}
