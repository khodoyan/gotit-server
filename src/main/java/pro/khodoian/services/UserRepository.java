package pro.khodoian.services;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pro.khodoian.models.User;

/**
 * JPA interface designed to get access to Relations
 *
 * @author eduardkhodoyan
 */
@Component("userRepository")
@Repository
public interface UserRepository extends CrudRepository<User, String> {
    @Override
    User findOne(String username);

    @Override
    User save(User user);

    @Override
    void delete(String username);

    @Override
    void deleteAll();

    @Override
    boolean exists(String username);
}
