package pro.khodoian.services;

import org.springframework.data.repository.Repository;
import pro.khodoian.models.User;

/**
 * Created by eduardkhodoyan on 10/21/15.
 */
public interface UserRepository extends Repository<User, Long> {
    User findUserById(long id);
    User findUserByUsername(String username);
}
