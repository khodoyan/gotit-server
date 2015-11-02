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
}
