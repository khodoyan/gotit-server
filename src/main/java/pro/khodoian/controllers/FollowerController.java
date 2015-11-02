package pro.khodoian.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pro.khodoian.auth.OAuth2Configuration;
import pro.khodoian.models.FollowSettings;
import pro.khodoian.models.Follower;
import pro.khodoian.models.Relation;
import pro.khodoian.models.User;
import pro.khodoian.services.RelationRepository;
import pro.khodoian.services.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller class that manages people and provides followers and following people to authenticated user
 *
 * @author eduardkhodoyan
 */
@Controller
public class FollowerController {

    // TODO: Implement class

    public static final String CONTROLLER_PATH = "/followers";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RelationRepository relationRepository;

    /**
     * Adds two relation to the repository: direct and indirect.
     * - Direct one confirmed by patient and not confirmed by follower.
     * - All privacy settings provided in Follower are set to direct relation
     *
     * - Indirect one confirmed by follower and not confirmed by patient.
     * - Privacy settings of indirect relation are set to false
     *
     * @param follower Follower object with settings and relation provided by user
     * @return HttpStatus.OK if success, HttpStatus.BAD_REQUEST if bad data, HttpStatus.INTERNAL_SERVER_ERROR if
     *         unhandled error, HttpStatus.NOT_MODIFIED if already exists
     */
    @RequestMapping(value = CONTROLLER_PATH, method = RequestMethod.POST)
    public ResponseEntity<Follower> add(@RequestBody Follower follower) {
        try {
            // make relations from Follower object (validity check is handled in makeRelation... method)
            Relation directRelation = Relation.makeRelationDirect(follower);
            Relation indirectRelation = Relation.makeRelationIndirect(follower);
            if(directRelation == null || indirectRelation == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            String principal = OAuth2Configuration.getPrincipal();
            if (principal == null || principal.equals(""))
                throw new Exception("Not authenticated");

            Relation addedDirectRelation = null;
            Relation addedIndirectRelation = null;

                    Relation existingDirectRelation =
                    relationRepository.findOneByPatientAndFollower(principal, follower.getFollower());
            if (existingDirectRelation == null)
                addedDirectRelation = relationRepository.save(directRelation);
            Relation existingIndirectRelation =
                    relationRepository.findOneByPatientAndFollower(follower.getFollower(), principal);
            if (existingIndirectRelation == null)
                addedIndirectRelation = relationRepository.save(indirectRelation);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            // Error accessing the repository
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns Follower object for the given username with the User data and privacy settings
     *
     * @param followerUsername username for lookup
     * @return Follower object is success, HttpStatus.BAD_REQUEST if blank or null followerUsername provided,
     *         HttpStatus.NOT_FOUND if no such user or no connection established
     */
    @RequestMapping(value = CONTROLLER_PATH + "/{username}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Follower> get(@PathVariable("username") String followerUsername) {
        // validate input data
        if (followerUsername == null || followerUsername.equals(""))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            String principal = OAuth2Configuration.getPrincipal();
            // get user and check
            User followerUser = userRepository.findOne(followerUsername);
            if (followerUser == null) {
                Follower nullResult = null;
                return new ResponseEntity<>(nullResult, HttpStatus.OK);
            }

            // get direct relation and check
            Relation directRelation = relationRepository.findOneByPatientAndFollower(principal, followerUsername);
            if (directRelation == null){
                Follower nullResult = null;
                return new ResponseEntity<>(nullResult, HttpStatus.OK);
            }

            // get indirect relation and check
            Relation indirectRelation = relationRepository.findOneByPatientAndFollower(followerUsername, principal);
            if (indirectRelation == null){
                Follower nullResult = null;
                return new ResponseEntity<>(nullResult, HttpStatus.OK);
            }

            // success: make follower and return
            return new ResponseEntity<>(
                    Follower.makeFollower(followerUser, directRelation, indirectRelation.isConfirmed()),
                    HttpStatus.OK);
        } catch (Exception e) {
            // Error accessing the repository
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns ArrayList of Followers that are associated with authenticated principal. Both confirmed and non-confirmed
     * followers are returned
     *
     * @return ArrayList of Followers if successful, HttpStatus.INTERNAL_SERVER_ERROR if server error
     */
    @RequestMapping(value = CONTROLLER_PATH, method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Follower>> getAll() {
        String principal = OAuth2Configuration.getPrincipal();
        try {
            // check principal
            if (principal == null || principal.equals(""))
                throw new Exception("Principal not provided by security configuration");

            // get followers and following
            ArrayList<Relation> followMe = relationRepository.findByPatient(principal);
            ArrayList<Relation> iFollow = relationRepository.findByFollower(principal);
            //HashMap<String, User> userHashMap = userRepository.findByRelationPatient(principal);
            ArrayList<Follower> result = new ArrayList<>();

            // transform following to HashMap of confirmations
            HashMap iFollowMap = new HashMap<String, Boolean>();
            if (iFollow != null && followMe!= null) {
                for (Relation relation : iFollow){
                    if(!iFollowMap.containsKey(relation.getPatient()))
                        iFollowMap.put(relation.getPatient(), relation.isConfirmed());
                }

                for(Relation relation : followMe) {
                    boolean isConfirmedByFollower = false;
                    if(iFollowMap.containsKey(relation.getFollower())
                            && relation.getFollower() != null && !relation.getFollower().equals("")) {
                        isConfirmedByFollower = (Boolean) iFollowMap.get(relation.getFollower());
                    }
                    // TODO: optimise here by using one SQL request - NESTED SQL is terrible!!!
                    User followerUser = userRepository.findOne(relation.getFollower());

                    Follower follower = Follower.makeFollower(
                            followerUser,
                            relation,
                            isConfirmedByFollower);
                    result.add(follower);

                }
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            // Error accessing the repository
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates Relation in repository
     * Mandatory fields:
     * - follower.follower
     * - follower's all settings
     * username must match follower.getFollower()
     *
     * @param follower new settings (only updates privacy settings)
     * @return HttpStatus.OK if success, HttpStatus.BAD_REQUEST if invalid data,
     *         HttpStatus.NOT_FOUND if relation not found
     */
    @RequestMapping(value = CONTROLLER_PATH, method = RequestMethod.PUT)
    public ResponseEntity<Void> updateSettings(@RequestBody Follower follower) {
        String principal = OAuth2Configuration.getPrincipal();
        // validate input data
        if (follower == null || follower.getRelation() == null
                || follower.getFollower() == null || follower.getFollower().equals(""))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            // get Relation that is in the repository now for this username
            Relation directRelation = relationRepository.findOneByPatientAndFollower(principal, follower.getFollower());
            if (directRelation == null)
                // such Relation does not exist
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else {
                // update found Relation with new data
                Relation changedRelation = follower.getRelation();
                changedRelation.setId(directRelation.getId());
                changedRelation = relationRepository.save(changedRelation);
                if (changedRelation == null)
                    // failed updating: should not be the case
                    throw new Exception("Can't update record in the repository: should not be the case");
                else
                    // success
                    return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            // Error accessing the repository
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates Relation in repository by setting Relation.isConfirmed to true
     * @param username of Relation to be confirmed
     * @return HttpStatus.OK if success, HttpStatus.BAD_REQUEST if invalid data,
     *         HttpStatus.NOT_FOUND if relation not found
     */
    @RequestMapping(value = CONTROLLER_PATH + "/confirm/{username}", method = RequestMethod.POST)
    public ResponseEntity<Void> confirm(
            @PathVariable("username") String username,
            @RequestBody FollowSettings settings
    ) {
        String principal = OAuth2Configuration.getPrincipal();
        // validate input data
        if (username == null || username.equals("") || settings == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            // get Relation that is in the repository now for this username
            Relation directRelation = relationRepository.findOneByPatientAndFollower(principal, username);
            if (directRelation == null)
                // such Relation does not exist
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else {
                // update found Relation with new data
                directRelation.setIsConfirmed(true);
                directRelation.setIsFollowed(settings.getIsFollow());
                directRelation.setShareFeeling(settings.getShareFeeling());
                directRelation.setShareBloodSugar(settings.getShareBloodSugar());
                directRelation.setShareInsulin(settings.getShareInsulin());
                directRelation.setShareQuestions(settings.getShareQuestions());

                directRelation = relationRepository.save(directRelation);
                if (directRelation == null || !directRelation.isConfirmed())
                    // failed updating: should not be the case
                    throw new Exception("Can't update record in the repository: should not be the case");
                else
                    // success
                    return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            // Error accessing the repository
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes direct and indirect relations with user with passed username
     *
     * @param username of user, relations for which shall be deleted
     * @return HttpStatus.OK if successful or no relations, HttpStatus.BAD_REQUEST if blank or null username passed
     */
    @RequestMapping(value = CONTROLLER_PATH + "/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("username") String username) {
        try {
            // validate inputs
            if (username == null || username.equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // delete direct and indirect relations

            String principal = OAuth2Configuration.getPrincipal();
            Relation direct = relationRepository.findOneByPatientAndFollower(principal, username);
            Relation indirect = relationRepository.findOneByPatientAndFollower(username, principal);
            if (direct != null)
                relationRepository.delete(direct.getId());
            if (indirect != null)
                relationRepository.delete(indirect.getId());
            //relationRepository.deleteByPatientAndFollower(principal, username); // direct relation
            //relationRepository.deleteByPatientAndFollower(username, principal); // indirect relation
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            // Error accessing the repository
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes all entries in Relations table of database
     * Method used for testing purposes only. Not accessible from controller
     */
    @RequestMapping(value = CONTROLLER_PATH + "/delete_all", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAll() {
        relationRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
