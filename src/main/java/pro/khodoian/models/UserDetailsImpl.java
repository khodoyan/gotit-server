package pro.khodoian.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Manual implementation of UserDetails service
 *
 * @author eduardkhodoyan
 */
public class UserDetailsImpl implements UserDetails {

    @Id
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private boolean enabled;
    ArrayList<Authority> authorities;

    protected UserDetailsImpl() {}

    public static UserDetailsImpl makeUserDetailsImpl(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().trim().equals("")
                || userDetails.getPassword() == null || userDetails.getPassword().equals(""))
            return null;
        UserDetailsImpl result = new UserDetailsImpl();
        result.username = userDetails.getUsername();
        result.password = new BCryptPasswordEncoder().encode(userDetails.getPassword());
        result.enabled = true; // TODO: consider implementing disabling account
        result.authorities = new ArrayList<>();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            String string = authority.getAuthority();
            if (string != null && !string.equals("") &&
                    (string.equals(Authority.ADMIN) || string.equals(Authority.PATIENT) ||
                            string.equals(Authority.FOLLOWER))) {
                result.authorities.add(new Authority(string));
            }
        }
        return result;
    }

    public static UserDetailsImpl makeUserDetailsImpl(SignupUser signupUser) {
        if (signupUser == null || !signupUser.isValid())
            return null;
        UserDetailsImpl result = new UserDetailsImpl();
        result.username = signupUser.getUsername();
        result.password = new BCryptPasswordEncoder().encode(signupUser.getPassword());
        result.enabled = true; // TODO: consider implementing disabling account
        result.setAuthorities(signupUser.role);
        return result;
    }

    @Override
    public Collection<Authority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(ArrayList<Authority> authorities) {
        this.authorities = authorities;
    }

    public void setAuthorities(SignupUser.Role role) {
        ArrayList<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(Authority.FOLLOWER));
        if (role == SignupUser.Role.PATIENT || role == SignupUser.Role.ADMIN)
            authorities.add(new Authority(Authority.PATIENT));
        if (role == SignupUser.Role.ADMIN)
            authorities.add(new Authority(Authority.ADMIN));
        setAuthorities(authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasAuthority(String authorityName) {
        if (authorities != null) {
            for (Authority authority : authorities) {
                if (authority != null && authority.getAuthority() != null
                        && authority.getAuthority().equals(authorityName))
                    return true;
            }
        }
        return false;
    }
}
