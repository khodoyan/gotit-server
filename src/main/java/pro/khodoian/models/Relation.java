package pro.khodoian.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pro.khodoian.auth.OAuth2Configuration;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Entity for keeping relationships between users.
 * Two entries is created for both Patient and Follower
 *
 * @author eduardkhodoyan
 */
@Entity
public class Relation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String patient;

    @JsonIgnore
    @OneToMany(targetEntity = Post.class, mappedBy = "username")
    private Set<Post> posts;

    @Column(nullable = false)
    private String follower;

    @Column(nullable = false)
    private boolean isConfirmed;

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

    public Relation() {}

    public Relation(String patient, String follower, boolean isConfirmed, boolean isFollowed,
                    boolean shareFeeling, boolean shareBloodSugar, boolean shareInsulin, boolean shareQuestions) {
        this.patient = patient;
        this.follower = follower;
        this.isConfirmed = isConfirmed;
        this.isFollowed = isFollowed;
        this.shareFeeling = shareFeeling;
        this.shareBloodSugar = shareBloodSugar;
        this.shareInsulin = shareInsulin;
        this.shareQuestions = shareQuestions;
    }

    /**
     * Factory method to make direct Relation from Follower received from client.
     * Mandatory fields: follower
     * Default fields (will be ignored):
     * - confirmedByPatient
     * - confirmedByFollower
     *
     * @param follower data received from remote client
     * @return Relation is data is valid, null if mandatory field not provided
     */
    public static Relation makeRelationDirect(Follower follower) {
        // check follower validity
        String principal = OAuth2Configuration.getPrincipal();
        if (follower == null || principal == null || principal.equals("")
                || follower.getFollower() == null || follower.getFollower().equals(""))
            return null;

        Relation relation = new Relation();
        relation.patient = principal;
        relation.follower = follower.getFollower();
        relation.isConfirmed = true;
        relation.isFollowed = follower.isFollowed();
        relation.shareFeeling = follower.isShareFeeling();
        relation.shareBloodSugar = follower.isShareBloodSugar();
        relation.shareInsulin = follower.isShareInsulin();
        relation.shareQuestions = follower.isShareQuestions();
        return relation;
    }

    /**
     * Factory method to make indirect Relation from Follower received from client.
     * Mandatory fields: follower
     * Default fields (will be ignored):
     * - confirmedByPatient
     * - confirmedByFollower
     * - all settings will be set to false
     *
     * @param follower data received from remote client
     * @return Relation is data is valid, null if mandatory field not provided
     */
    public static Relation makeRelationIndirect(Follower follower) {
        // check follower validity
        String principal = OAuth2Configuration.getPrincipal();
        if (follower == null || principal == null || principal.equals("")
                || follower.getFollower() == null || follower.getFollower().equals(""))
            return null;

        Relation relation = new Relation();
        relation.patient = follower.getFollower();
        relation.follower = principal;
        relation.isConfirmed = false;
        relation.isFollowed = false;
        relation.shareFeeling = false;
        relation.shareBloodSugar = false;
        relation.shareInsulin = false;
        relation.shareQuestions = false;
        return relation;
    }

    public Relation copyAndSetId(Relation source, Long id) {
        Relation result = source.clone();
        result.id = id;
        return result;
    }

    @Override
    public Relation clone() {
        Relation result = new Relation();
        result.patient = this.patient;
        result.follower = this.follower;
        result.isConfirmed = this.isConfirmed;
        result.isFollowed = this.isFollowed;
        result.shareFeeling = this.shareFeeling;
        result.shareBloodSugar = this.shareBloodSugar;
        result.shareInsulin = this.shareInsulin;
        result.shareQuestions = this.shareQuestions;
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(boolean isConfirmedByPatient) {
        this.isConfirmed = isConfirmedByPatient;
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

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
}
