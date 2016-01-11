package careshare.nominationService.repo;

import careshare.nominationService.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NominationRepo extends JpaRepository<Nomination, Long> {
    String FIND_AUTHORS = "SELECT author_id, MAX(timestamp) FROM nomination WHERE patient_id = ?1 GROUP BY author_id";
    @Query(value = FIND_AUTHORS, nativeQuery = true)
    List<Object[]> findAuthorIdsByPatientId(String patientId);
    // can't auto-magically map a native query to a POJO, need to do it manually in our controller :(

    List<Nomination> findByPatientIdAndResourceType(String patientId, String resourceType);

    List<Nomination> findByPatientIdAndResourceId(String patientId, String resourceId);

    List<Nomination> findByPatientIdAndAuthorIdAndResourceType(String patientId, String authorId, String resourceType);

    Nomination findByPatientIdAndAuthorIdAndResourceId(String patientId, String authorId, String resourceId);
}
