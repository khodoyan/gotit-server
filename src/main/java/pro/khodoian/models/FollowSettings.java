package pro.khodoian.models;

/**
 * POJO class for communicating privacy settings with client
 *
 * @author eduardkhodoyan
 */
public class FollowSettings {
    private boolean isFollow;
    private boolean shareFeeling;
    private boolean shareBloodSugar;
    private boolean shareInsulin;
    private boolean shareQuestions;

    public FollowSettings() {
    }

    public FollowSettings(boolean isFollow,
                          boolean shareFeeling,
                          boolean shareBloodSugar,
                          boolean shareInsulin,
                          boolean shareQuestions) {
        this.isFollow = isFollow;
        this.shareFeeling = shareFeeling;
        this.shareBloodSugar = shareBloodSugar;
        this.shareInsulin = shareInsulin;
        this.shareQuestions = shareQuestions;
    }

    public boolean getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }

    public boolean getShareFeeling() {
        return shareFeeling;
    }

    public void setShareFeeling(boolean shareFeeling) {
        this.shareFeeling = shareFeeling;
    }

    public boolean getShareBloodSugar() {
        return shareBloodSugar;
    }

    public void setShareBloodSugar(boolean shareBloodSugar) {
        this.shareBloodSugar = shareBloodSugar;
    }

    public boolean getShareInsulin() {
        return shareInsulin;
    }

    public void setShareInsulin(boolean shareInsulin) {
        this.shareInsulin = shareInsulin;
    }

    public boolean getShareQuestions() {
        return shareQuestions;
    }

    public void setShareQuestions(boolean shareQuestions) {
        this.shareQuestions = shareQuestions;
    }
}
