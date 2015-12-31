package careshare.nominationService.repo;

import careshare.nominationService.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface NominationRepo extends JpaRepository<Nomination, Long> {
    Collection<Nomination> findByCarePlanIdAndResourceType(String carePlanId, String resourceType);

    Nomination findByCarePlanIdAndResourceTypeAndId(String carePlanId, String resourceType, Long id);
}
