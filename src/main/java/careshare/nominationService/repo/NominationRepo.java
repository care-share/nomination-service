package careshare.nominationService.repo;

import careshare.nominationService.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NominationRepo extends JpaRepository<Nomination, Long> {
    String FIND_AUTHORS = "SELECT author_id, MAX(timestamp) FROM nomination WHERE care_plan_id = ?1 GROUP BY author_id";
    @Query(value = FIND_AUTHORS, nativeQuery = true)
    List<Object[]> findAuthorIdsByCarePlanId(String carePlanId);
    // can't auto-magically map a native query to a POJO, need to do it manually in our controller :(

    List<Nomination> findByCarePlanIdAndResourceType(String carePlanId, String resourceType);

    List<Nomination> findByResourceId(String resourceId);

    List<Nomination> findByCarePlanIdAndAuthorIdAndResourceType(String carePlanId, String authorId, String resourceType);

    Nomination findByAuthorIdAndResourceId(String authorId, String resourceId);
}
