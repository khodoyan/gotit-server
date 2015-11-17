package pro.khodoian.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.models.*;
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

    @Autowired
    UserDetailsManager userDetailsManager;

    @PreAuthorize("hasRole('ROLE_PATIENT')")
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

    @PreAuthorize("hasRole('ROLE_PATIENT')")
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
        try {
            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");

            // check if user exists
            if (!userRepository.exists(principal))
                throw new Exception("Principal does not exist in the database");

            // declare raw posts and result ArrayLists
            ArrayList<Post> rawPosts;
            ArrayList<Post> result = new ArrayList<>();

            // get followed users
            ArrayList<Relation> followedRelations =
                    relationRepository.findByFollower(principal);

            // get raw posts for followed users
            ArrayList<String> followedUsernames = new ArrayList<>();
            followedUsernames.add(principal);

            if (followedRelations != null) {
                for (Relation relation : followedRelations) {
                    if (relation != null)
                        followedUsernames.add(relation.getPatient());
                }
            }

            rawPosts = postRepository.findByUsernameIn(
                    new Sort(Sort.Direction.DESC, "timestamp"),
                    followedUsernames
            );

            // proceed privacy settings check
            if (rawPosts != null)
                result = checkAuthorities(rawPosts, principal);

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/page/{page}", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Post>> getPage(@PathVariable("page") int page) {
        try {
            // get principal and set it to post
            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Can't get principal for the transaction");

            // check if user exists
            if (!userRepository.exists(principal))
                throw new Exception("Principal does not exist in the database");

            // declare raw posts and result ArrayLists
            Page<Post> rawPosts;
            ArrayList<Post> result = new ArrayList<>();

            // get followed users
            ArrayList<Relation> followedRelations =
                    relationRepository.findByFollower(principal);

            // get raw posts for followed users
            ArrayList<String> followedUsernames = new ArrayList<>();
            followedUsernames.add(principal);

            if (followedRelations != null) {
                for (Relation relation : followedRelations) {
                    if (relation != null)
                        followedUsernames.add(relation.getPatient());
                }
            }

            rawPosts = postRepository.findByUsernameIn(
                    new PageRequest(page, PAGE_SIZE, new Sort(Sort.Direction.DESC, "timestamp")),
                    followedUsernames
            );

            // proceed privacy settings check
            if (rawPosts != null)
                result = checkAuthorities(rawPosts, principal);

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

    @RequestMapping(value = CONTROLLER_PATH + "/blood_sugar/{username}")
    public ResponseEntity<ArrayList<BloodSugar>> getBloodSugarByUser(@PathVariable("username") String username) {
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
            if (!username.equals(principal) && (relation == null || !relation.isShareBloodSugar()
                    || !relation.isConfirmed()))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            // get data, check permissions, and return result
            ArrayList<BloodSugar> result = new ArrayList<>();
            ArrayList<Post> rawPosts = postRepository.findByUsername(
                    new Sort(Sort.Direction.ASC, "timestamp"),
                    username
            );
            if (rawPosts != null) {
                for (Post post : rawPosts) {
                    Post checkedPost = post.checkShared();
                    checkedPost = checkedPost.checkAuthorities(principal, relationRepository);
                    if (checkedPost != null)
                        result.add(new BloodSugar(post.getTimestamp(), post.getBloodSugar()));
                }
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = CONTROLLER_PATH + "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
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

    /**
     * Deletes all posts in repository. Available only to users with admin rights
     *
     * @return HttpStatus.OK if successful, HttpStatus.UNAUTHORIZED if no admin rights
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = CONTROLLER_PATH + "/delete_all", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAll() {
        try {
            String principal = OAuth2Configuration.getPrincipal();
            UserDetailsImpl user = UserDetailsImpl
                    .makeUserDetailsImpl(userDetailsManager.loadUserByUsername(principal));
            if (user == null || !user.hasAuthority(Authority.ADMIN))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            postRepository.deleteAll();
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
