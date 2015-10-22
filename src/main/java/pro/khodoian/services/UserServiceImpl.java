package pro.khodoian.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pro.khodoian.models.User;

/**
 *
 * @author eduardkhodoyan
 */
@Component("userService")
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(long id) {
        Assert.isTrue(id >= 0, "Id can't be negative");
        return userRepository.findUserById(id);
    }

    @Override
    public User getUser(String username) {
        Assert.notNull(username, "User must not be null");
        return userRepository.findUserByUsername(username);
    }
}
