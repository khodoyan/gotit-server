package pro.khodoian.clientmodels;

import pro.khodoian.models.User;

/**
 * Return type for client requests
 * JSON compatible POJO class
 *
 * @author eduardkhodoyan
 */
public class ClientUser {
    private long id;
    private boolean isPatient;
    private String username;
    private String firstName;
    private String lastName;
    private long birthDay;
    private String medicalRecordNumber;
    private String userpicFilename;
    private boolean isConfirmedByYou;
    private boolean isConfirmedByFriend;
    private boolean isFollowed;
    private boolean shareFeeling;
    private boolean shareBloodSugar;
    private boolean shareInsulin;
    private boolean shareQuestions;

    //UserService userService = new UserServiceImpl();

    protected ClientUser(){};

    public ClientUser makeClientUser(User requestedUser, String principal) {
        // TODO: implement
        return null;
    }

    public ClientUser makeClientUser (String requestedUsername, String principal) {
        User requestedUser = null; // userService.get(requestedUsername);
        if (requestedUser == null)
            // user not found
            return null;
        else {
            return makeClientUser(requestedUser, principal);
        }
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPatient() {
        return isPatient;
    }

    public void setIsPatient(boolean isPatient) {
        this.isPatient = isPatient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getUserpicFilename() {
        return userpicFilename;
    }

    public void setUserpicFilename(String userpicFilename) {
        this.userpicFilename = userpicFilename;
    }

    public boolean isConfirmedByYou() {
        return isConfirmedByYou;
    }

    public void setIsConfirmedByYou(boolean isConfirmedByYou) {
        this.isConfirmedByYou = isConfirmedByYou;
    }

    public boolean isConfirmedByFriend() {
        return isConfirmedByFriend;
    }

    public void setIsConfirmedByFriend(boolean isConfirmedByFriend) {
        this.isConfirmedByFriend = isConfirmedByFriend;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
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