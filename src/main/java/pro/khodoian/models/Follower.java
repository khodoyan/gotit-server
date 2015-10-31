package pro.khodoian.models;

import pro.khodoian.auth.OAuth2Configuration;

/**
 * Return type for client requests
 * JSON compatible POJO class
 *
 * @author eduardkhodoyan
 */
public class Follower {
    private String follower; // mandatory
    private String firstName;
    private String lastName;
    private String userpicFilename;
    private boolean confirmedByPatient;
    private boolean confirmedByFollower;
    private boolean followed;
    private boolean shareFeeling;
    private boolean shareBloodSugar;
    private boolean shareInsulin;
    private boolean shareQuestions;

    //UserService userService = new UserServiceImpl();

    protected Follower(){}

    /**
     * Factory method to create Follower object from User object and direct Relation
     * Mandatory fields:
     * - followerUser, directRelation, followerUser.username
     * Username in both arguments must match
     *
     * @param followerUser follower User object
     * @param directRelation direct Relation containing confirmations and privacy settings
     * @param isConfirmedByFollower indicates if Relation confirmed by follower (default value is false)
     * @return Follower object made of Follower object and direct Relation, or null if data is invalid or missing
     */
    public static Follower makeFollower(User followerUser, Relation directRelation, boolean isConfirmedByFollower) {
        // validate data
        if (followerUser == null
                || followerUser.getUsername() == null || followerUser.getUsername().equals("")
                || directRelation == null
                || !followerUser.getUsername().equals(directRelation.getFollower()))
            return null;
        // create and fill user
        Follower follower = new Follower();
        follower.setFollower(followerUser.getUsername());
        follower.setFirstName(followerUser.getFirstName());
        follower.setLastName(followerUser.getLastName());
        follower.setUserpicFilename(followerUser.getUserpicFilename());
        follower.setIsConfirmedByPatient(directRelation.isConfirmed());
        follower.setIsConfirmedByFollower(isConfirmedByFollower);
        follower.setIsFollowed(directRelation.isFollowed());
        follower.setShareFeeling(directRelation.isShareFeeling());
        follower.setShareBloodSugar(directRelation.isShareBloodSugar());
        follower.setShareInsulin(directRelation.isShareInsulin());
        follower.setShareQuestions(directRelation.isShareQuestions());
        return follower;
    }

    public static Follower makeFollower(User followerUser, Relation directRelation) {
        return makeFollower(followerUser, directRelation, false);
    }

    public Relation getRelation() {
        String principal = OAuth2Configuration.getPrincipal();
        if (principal == null || principal.equals("") || this.follower == null || this.follower.equals(""))
            return null;
        Relation relation = new Relation();
        relation.setPatient(principal);
        relation.setFollower(this.follower);
        relation.setIsConfirmed(this.isConfirmedByPatient());
        relation.setIsFollowed(this.followed);
        relation.setShareFeeling(this.shareFeeling);
        relation.setShareBloodSugar(this.shareBloodSugar);
        relation.setShareInsulin(this.shareInsulin);
        relation.setShareQuestions(this.shareQuestions);
        return relation;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserpicFilename() {
        return userpicFilename;
    }

    public void setUserpicFilename(String userpicFilename) {
        this.userpicFilename = userpicFilename;
    }

    public boolean isConfirmedByPatient() {
        return confirmedByPatient;
    }

    public void setIsConfirmedByPatient(boolean isConfirmedByYou) {
        this.confirmedByPatient = isConfirmedByYou;
    }

    public boolean isConfirmedByFollower() {
        return confirmedByFollower;
    }

    public void setIsConfirmedByFollower(boolean isConfirmedByFriend) {
        this.confirmedByFollower = isConfirmedByFriend;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setIsFollowed(boolean isFollowed) {
        this.followed = isFollowed;
    }

    public boolean isShareFeeling() {
        return shareFeeling;
    }

    public void setShareFeeling(boolean shareFeeling) {
        this.shareFeeling = shareFeeling;
    }

    public boolean isShareBloodSugar() {
        return shareBloodSugar;
    }

    public void setShareBloodSugar(boolean shareBloodSugar) {
        this.shareBloodSugar = shareBloodSugar;
    }

    public boolean isShareInsulin() {
        return shareInsulin;
    }

    public void setShareInsulin(boolean shareInsulin) {
        this.shareInsulin = shareInsulin;
    }

    public boolean isShareQuestions() {
        return shareQuestions;
    }

    public void setShareQuestions(boolean shareQuestions) {
        this.shareQuestions = shareQuestions;
    }

}