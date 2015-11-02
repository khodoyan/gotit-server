package pro.khodoian.services;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import pro.khodoian.models.Relation;

import java.util.ArrayList;

/**
 * JPA interface designed to get access to Relations
 *
 * @author eduardkhodoyan
 */
public interface RelationRepository extends CrudRepository<Relation, Long> {
    Relation findOneByPatientAndFollower(String patient, String follower);

    //Void deleteByPatientAndFollower(String patient, String follower);
    ArrayList<Relation> findByPatient(String patient);
    ArrayList<Relation> findByFollower(String follower);

    //ArrayList<Relation> findByFollowerAndIsConfirmed(String follower, boolean isConfirmed);

    @Override
    Relation save(Relation relation);

    @Override
    void delete(Long id);

}
