package pro.khodoian.models;

import javax.persistence.*;

/**
 * Entity for keeping relationships between users.
 * Two entries is created for both Patient and Follower
 */
@Entity
public class Relation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String patient;

    @Column(nullable = false)
    private String follower;

    @Column(nullable = false)
    private boolean isConfirmedByPatient;

    @Column(nullable = false)
    private boolean isConfirmedByFollower;

    @Column(nullable = false)
    private boolean isFollowed;
    @Column(nullable = false)
    private boolean shareFeeling;
    @Column(nullable = false)
    private boolean shareBloodSugar;
    @Column(nullable = false)
    private boolean shareInsulin;
    @Column(nullable = false)
    private boolean shareQuestions;
}
