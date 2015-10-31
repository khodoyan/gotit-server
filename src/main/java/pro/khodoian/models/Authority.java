package pro.khodoian.models;

import org.springframework.security.core.GrantedAuthority;

/**
 * Model class for authority management
 */
public class Authority implements GrantedAuthority {
    public static final String PATIENT = "ROLE_PATIENT";
    public static final String FOLLOWER = "ROLE_FOLLOWER";
    public static final String ADMIN = "ROLE_ADMIN";

    private String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

}
