package pro.khodoian.models;

import javax.persistence.*;

/**
 * Created by eduardkhodoyan on 10/21/15.
 */
@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String hash;

    protected User() {}

    public User(String username, String hash) {
        this.username = username;
        this.hash = hash;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, username=%s]", id, username);
    }
}
