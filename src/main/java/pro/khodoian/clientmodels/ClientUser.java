package pro.khodoian.clientmodels;

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
}