package pro.khodoian.models;

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

    @ManyToOne(targetEntity = Relation.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
}
