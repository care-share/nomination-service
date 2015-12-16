package careshare.nominationService.repo;

import careshare.nominationService.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;


public interface ConditionRepo extends JpaRepository<Condition, Long> {
    Collection<Condition> findByCareplan(String careplan);
}