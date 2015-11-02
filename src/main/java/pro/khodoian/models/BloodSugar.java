package pro.khodoian.models;

/**
 * Simple POJO class for providing bloodSugar records to client
 *
 * @author eduardkhodoyan
 */
public class BloodSugar {
    private long timestamp;
    private float bloodSugar;

    public BloodSugar(long timestamp, float bloodSugar) {
        this.timestamp = timestamp;
        this.bloodSugar = bloodSugar;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(float bloodSugar) {
        this.bloodSugar = bloodSugar;
    }
}
