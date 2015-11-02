package pro.khodoian.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import pro.khodoian.services.RelationRepository;

import javax.persistence.*;

/**
 * Entity for keeping User values in Spring database and for communication with PostClient class on client
 *
 * @author eduardkhodoyan
 */
@Entity
public class Post {

    public enum Feeling {BAD, OKAY, GOOD}

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @ManyToOne(targetEntity = Relation.class, fetch = FetchType.LAZY)
    private Relation relation;

    @Column(nullable = false)
    private String username;

    @Column
    private long updatedAt;
    @Column
    private long deletedAt;
    @Column(nullable = false)
    private long timestamp;
    @Column(nullable = false)
    private boolean isShared;
    @Column
    private Feeling feeling;
    @Column
    private float bloodSugar;
    @Column
    private boolean administeredInsulin;
    @Column
    private String questionnaire;

    protected Post() {
    }

    public Post(String username, long updatedAt, long deletedAt, long timestamp,
                boolean isShared, Feeling feeling, float bloodSugar, boolean administeredInsulin,
                String questionnaire) {
        this.username = username;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.timestamp = timestamp;
        this.isShared = isShared;
        this.feeling = feeling;
        this.bloodSugar = bloodSugar;
        this.administeredInsulin = administeredInsulin;
        this.questionnaire = questionnaire;
    }

    private void fillRelation(String follower, RelationRepository relationRepository) {
        if (relation == null
                && username != null && !username.equals("")
                && follower != null && !follower.equals("")
                && !follower.equals(username))
            relation = relationRepository.findOneByPatientAndFollower(username, follower);
    }

    public Post checkAuthorities(String follower, RelationRepository relationRepository) {
        //Hibernate.initialize(this.getRelation());
        fillRelation(follower, relationRepository);
        if (follower == null || follower.equals(""))
            return null;

        boolean isSelf = follower.equals(username);
        if (!isSelf && relation == null)
            return null;
        Post result = new Post();
        result.id = id;
        result.relation = relation;
        result.username = username;
        result.updatedAt = updatedAt;
        result.deletedAt = deletedAt;
        result.timestamp = timestamp;
        result.isShared = isShared;
        if (isSelf || relation.isShareFeeling())
            result.feeling = feeling;
        if (isSelf || relation.isShareBloodSugar())
            result.bloodSugar = bloodSugar;
        if (isSelf || relation.isShareInsulin())
            result.administeredInsulin = administeredInsulin;
        if (isSelf || relation.isShareQuestions())
            result.questionnaire = questionnaire;
        return result;
    }

    public Post checkShared() {
        if(this.isShared)
            return this;
        else
            return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean getIsShared() {
        return isShared;
    }

    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }

    public Post.Feeling getFeeling() {
        return feeling;
    }

    public void setFeeling(Post.Feeling feeling) {
        this.feeling = feeling;
    }

    public float getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(float bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public boolean getAdministeredInsulin() {
        return administeredInsulin;
    }

    public void setAdministeredInsulin(boolean administeredInsulin) {
        this.administeredInsulin = administeredInsulin;
    }

    public String getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(String questionnaire) {
        this.questionnaire = questionnaire;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Relation getRelation() {
        return relation;
    }
}
