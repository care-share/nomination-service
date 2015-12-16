package careshare.nominationService.repo;

import careshare.nominationService.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;


public interface GoalRepo extends JpaRepository<Goal, Long> {
    Collection<Goal> findByCareplan(String careplan);
}