package careshare.nominationService.repo;

import careshare.nominationService.model.MedicationOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;


public interface MedicationOrderRepo extends JpaRepository<MedicationOrder, Long> {
    Collection<MedicationOrder> findByCareplan(String careplan);
	//void delete(MedicationOrder mo);
	
}

