package careshare.nominationService.repo;

import careshare.nominationService.model.DiagnosticOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;


public interface DiagnosticOrderRepo extends JpaRepository<DiagnosticOrder, Long> {
    Collection<DiagnosticOrder> findByCareplan(String careplan);
}