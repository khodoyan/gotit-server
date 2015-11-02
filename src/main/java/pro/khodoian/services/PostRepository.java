package pro.khodoian.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import pro.khodoian.models.Post;

import java.util.ArrayList;

/**
 * JPA interface designed to get access to Posts
 *
 * @author eduardkhodoyan
 */
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    @Override
    Post save(Post post);

    @Override
    ArrayList<Post> findAll(Sort sort);

    @Override
    Page<Post> findAll(Pageable pageable);

    ArrayList<Post> findByUsernameIn(Sort sort, ArrayList<String> username);
    Page<Post> findByUsernameIn(Pageable pageable, ArrayList<String> username);

    @Override
    <S extends Post> ArrayList<S> save(Iterable<S> entities);



    ArrayList<Post> findByUsername(String username);
    Page<Post> findByRelationFollowerAndRelationIsConfirmed(Pageable pageable, String username, boolean isConfirmed);
    ArrayList<Post> findByUsername(Sort sort, String username);
}
