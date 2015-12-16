package careshare.nominationService.repo;

import careshare.nominationService.model.ProcedureRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;


public interface ProcedureRequestRepo extends JpaRepository<ProcedureRequest, Long> {
    Collection<ProcedureRequest> findByCareplan(String careplan);
}