package pro.khodoian.models;

import com.google.gson.annotations.SerializedName;

/**
 * Simple POJO class to pass new user details from client to server side.
 *
 * @author eduardkhodoyan
 */
public class SignupUser {

    // TODO: update to reasonable length
    public static final int MIN_PASSWORD_LENGTH = 4;

    public enum Role {
        PATIENT,
        FOLLOWER,
        ADMIN}


    private String username;
    private String password;
    private boolean isPatient;
    private String firstname;
    private String lastname;
    private long birthday;
    private String medicalRecordNumber;
    private String userpicFilename;
    Role role;

    protected SignupUser() {}

    public static SignupUser makeUser(
            String username, // mandatory, trimmed
            String password, // mandatory, no less chars than MIN_PASSWORD_LENGTH
            boolean isPatient,
            String firstname,
            String lastname,
            long birthday,
            String medicalRecordsNumber,
            String userpicFilename,
            Role role
    ){
        SignupUser result = new SignupUser();
        // validate data:

        if (username == null || username.trim().equals("") // username
                || password == null || password.length() < MIN_PASSWORD_LENGTH) // password
            return null;

        // fill in data and return it
        result.username = username.trim();
        result.password = password;
        result.isPatient = isPatient;
        result.firstname = (firstname == null) ? null : firstname.trim();
        result.lastname = (lastname == null) ? null : lastname.trim();
        result.birthday = birthday;
        result.medicalRecordNumber = (lastname == null) ? null : medicalRecordsNumber.trim();
        result.userpicFilename = (userpicFilename == null) ? null : userpicFilename.trim();
        result.role = role;
        return result;
    }

    public User toUser() {
        return new User(isPatient, username, firstname, lastname, birthday,
                medicalRecordNumber, userpicFilename);
    }

    public boolean isValid() {
        return !(username == null || username.trim().equals("") // username
                || password == null || password.length() < MIN_PASSWORD_LENGTH); // password
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsPatient() {
        return isPatient;
    }

    public void setIsPatient(boolean isPatient) {
        this.isPatient = isPatient;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
