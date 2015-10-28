package pro.khodoian.services;

import org.springframework.data.repository.Repository;
import pro.khodoian.models.Relation;

import java.util.ArrayList;

/**
 * JPA interface designed to get access to Relations
 *
 * @author eduardkhodoyan
 */
public interface RelationRepository extends Repository<Relation, Long> {
    Relation findOneByPatientAndFollower(String patient, String follower);
    ArrayList<Relation> findByPatient(String patient);
    ArrayList<Relation> findByFollower(String follower);
}
