package careshare.nominationService.repo;

import careshare.nominationService.model.OneRing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;


public interface OneRingRepo extends JpaRepository<OneRing, Long> {
    Collection<OneRing> findByCareplanAndNominationFor(String careplan, String nominationFor);
	OneRing findByCareplanAndNominationForAndId(String careplan, String nominationFor, Long id);
	Collection<OneRing> findByCareplanAndNominationForAndAction(String careplan, String nominationFor, String action);
}