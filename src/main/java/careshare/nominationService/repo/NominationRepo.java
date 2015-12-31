package careshare.nominationService.repo;

import careshare.nominationService.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface NominationRepo extends JpaRepository<Nomination, Long> {
    String FIND_AUTHORS = "SELECT DISTINCT author_id FROM nomination WHERE care_plan_id = ?1";
    @Query(value = FIND_AUTHORS, nativeQuery = true)
    Collection<String> findAuthorIdsByCarePlanId(String carePlanId);

    Collection<Nomination> findByCarePlanIdAndAuthorIdAndResourceType(String carePlanId, String authorId, String resourceType);

    Nomination findByCarePlanIdAndAuthorIdAndResourceTypeAndId(String carePlanId, String authorId, String resourceType, Long id);
}
