package careshare.nominationService.controller;

import careshare.nominationService.model.CarePlan;
import careshare.nominationService.model.Condition;
import careshare.nominationService.model.DiagnosticOrder;
import careshare.nominationService.model.Goal;
import careshare.nominationService.model.MedicationOrder;
import careshare.nominationService.model.ProcedureRequest;
import careshare.nominationService.repo.MedicationOrderRepo;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author kcrouch
 */
@RestController
@RequestMapping("/careplan/{careplan}")
class CarePlanController {

  private final MedicationOrderRepo medOrderRepo;

  @Autowired
  CarePlanController(MedicationOrderRepo mor) {
	this.medOrderRepo = mor;
  }

  @RequestMapping(method = RequestMethod.GET)
  CarePlan generateCareplan(@PathVariable String careplan) {
	Collection<Condition> conditions = new ArrayList<>();
	Collection<DiagnosticOrder> diagnosticOrders = new ArrayList<>();
	Collection<Goal> goals = new ArrayList<>();
	Collection<MedicationOrder> medOrders = this.medOrderRepo.findByCareplan(careplan);
	Collection<ProcedureRequest> procedureRequests = new ArrayList<>();
	
	CarePlan plan = new CarePlan(careplan, conditions, diagnosticOrders,
	goals, medOrders, procedureRequests);
//	CarePlan plan = new CarePlan(careplan, medOrders, null, null);
	return plan;
  }
  
  
}

@RestController
@RequestMapping("/careplan/{careplan}/medication-orders")
class MedicationOrderController {

	private final MedicationOrderRepo medOrderRepo;


	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(@PathVariable String careplan, @RequestBody MedicationOrder input) {
		MedicationOrder result = 
				medOrderRepo.save(new MedicationOrder(input.getCareplan(),
				input.getAction(), input.getExisting(), input.getProposed(),
				input.getDiff()));

					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.setLocation(ServletUriComponentsBuilder
							.fromCurrentRequest().path("/{id}")
							.buildAndExpand(result.getId()).toUri());
					return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
  	}

	@RequestMapping(value = "/{nomId}", method = RequestMethod.GET)
	MedicationOrder readMedOrder(@PathVariable String careplan, @PathVariable Long nomId) {
		return this.medOrderRepo.findOne(nomId);
	}
	
	@RequestMapping(value="/{nomId}", method = RequestMethod.DELETE)
	void deleteNomination(@PathVariable Long nomId) {
	  MedicationOrder mo = this.medOrderRepo.findOne(nomId);
	  if(mo != null)
		this.medOrderRepo.delete(mo);
	}

	@RequestMapping(method = RequestMethod.GET)
	Collection<MedicationOrder> readMedOrders(@PathVariable String careplan) {
		return this.medOrderRepo.findByCareplan(careplan);
	}
	

	@Autowired
	MedicationOrderController(MedicationOrderRepo mor) {
		this.medOrderRepo = mor;
	}

}
