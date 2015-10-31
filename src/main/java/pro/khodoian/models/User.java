package pro.khodoian.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for keeping User values in Spring database
 */
@Entity
public class User {

    @OneToMany(mappedBy = "user")
    private Set<Relation> relations = new HashSet<>();

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private boolean isPatient;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    private long birthDay;

    @Column
    private String medicalRecordNumber;

    @Column
    private String userpicFilename;

    protected User() {}

    public User(boolean isPatient, String username, String firstName, String lastName, long birthDay,
                String medicalRecordNumber, String userpicFilename) {
        this.isPatient = isPatient;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.medicalRecordNumber = medicalRecordNumber;
        this.userpicFilename = userpicFilename;
    }

    /**
     * Factory method to make User from Follower received from remote client
     * mandatory field: username
     *
     * @param follower data received from remote client
     * @return User if all correct, null if mandatory field is missing
     */
    public static User makeUser(Follower follower) {
        if (follower == null || follower.getFollower() == null || follower.getFollower().equals(""))
            return null;
        User result = new User();
        result.username = follower.getFollower();
        result.firstName = follower.getFirstName();
        result.lastName = follower.getLastName();
        result.userpicFilename = follower.getUserpicFilename();
        return result;
    }

    public boolean getIsPatient() {
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
}
