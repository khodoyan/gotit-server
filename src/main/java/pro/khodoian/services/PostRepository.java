package pro.khodoian.services;

import org.springframework.data.repository.CrudRepository;
import pro.khodoian.models.Post;

import java.util.ArrayList;

/**
 * JPA interface designed to get access to Posts
 *
 * @author eduardkhodoyan
 */
public interface PostRepository extends CrudRepository<Post, Long> {

    ArrayList<Post> findByUsername(String username);
    ArrayList<Post> findByUsernamePatient(String username);
}
