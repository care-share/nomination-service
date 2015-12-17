package careshare.nominationService.controller;

import careshare.nominationService.model.ORCarePlan;
import careshare.nominationService.model.OneRing;
import careshare.nominationService.repo.OneRingRepo;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author kcrouch
 */
@RestController
@RequestMapping("/careplan")
class CarePlanController {

  private final OneRingRepo oneRingRepo;

  private static final String NF_CONDITION = "condition";
  private static final String NF_DIAG_ORDER = "diagnostic-order";
  private static final String NF_GOAL = "goal";
  private static final String NF_MED_ORDER = "medication-order";
  private static final String NF_PROC_REQUEST = "procedure-request";

  @Autowired
  CarePlanController(OneRingRepo orr) {
	this.oneRingRepo = orr;
  }

  @RequestMapping(value = "/{careplan}", method = RequestMethod.GET)
  ORCarePlan generateCareplan(@PathVariable String careplan) {
	Collection<OneRing> conditions = oneRingRepo.findByCareplanAndNominationFor(careplan, NF_CONDITION);
	Collection<OneRing> diagnosticOrders = oneRingRepo.findByCareplanAndNominationFor(careplan, NF_DIAG_ORDER);
	Collection<OneRing> goals = oneRingRepo.findByCareplanAndNominationFor(careplan, NF_GOAL);
	Collection<OneRing> medOrders = oneRingRepo.findByCareplanAndNominationFor(careplan, NF_MED_ORDER);
	Collection<OneRing> procedureRequests = oneRingRepo.findByCareplanAndNominationFor(careplan, NF_PROC_REQUEST);

	ORCarePlan plan = new ORCarePlan(careplan, conditions, diagnosticOrders,
			goals, medOrders, procedureRequests);
	return plan;
  }

  @RequestMapping("/{careplan}/{nomFor}")
  Collection<OneRing> gatherNoms(@PathVariable String careplan, @PathVariable String nomFor) {
	nomFor = lessEs(nomFor);
	Collection<OneRing> rings = oneRingRepo.findByCareplanAndNominationFor(careplan, nomFor);
	return rings;
  }

  @RequestMapping(value = "/{careplan}/{nomFor}", method = RequestMethod.PUT)
  ResponseEntity<?> add(@PathVariable String careplan, @PathVariable String nomFor, @RequestBody OneRing input) {
	nomFor = lessEs(nomFor);
	
	OneRing res = new OneRing(careplan, input.getAction(), nomFor,
			input.getExisting(), input.getProposed(), input.getDiff());
	res = oneRingRepo.save(res);

	HttpHeaders httpHeaders = new HttpHeaders();
	httpHeaders.setLocation(ServletUriComponentsBuilder
			.fromCurrentRequest().path("/{id}")
			.buildAndExpand(res.getId()).toUri());
	return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{careplan}/{nomFor}/{nomId}", method = RequestMethod.GET)
  OneRing readOneRing(@PathVariable String careplan, @PathVariable String nomFor, @PathVariable Long nomId) {
	nomFor = lessEs(nomFor);

	OneRing ring = oneRingRepo.findByCareplanAndNominationForAndId(careplan, nomFor, nomId);
	if (ring == null) {
	  throw new ItemNotFoundException();
	} else {
	  return ring;
	}
  }

  @RequestMapping(value = "/{careplan}/{nomFor}/{nomId}",
		  method = RequestMethod.DELETE)
  void deleteNomination(@PathVariable String careplan, @PathVariable String nomFor,
		  @PathVariable Long nomId) {
	nomFor = lessEs(nomFor);

	OneRing ring = oneRingRepo.findByCareplanAndNominationForAndId(careplan, nomFor, nomId);

	if (ring != null) {
	  oneRingRepo.delete(ring);
	} else {
	  throw new ItemNotFoundException();
	}
  }

  private String lessEs(String nomFor) {
	if (nomFor.endsWith("s")) {
	  return nomFor.substring(0, nomFor.length() - 1);
	} else {
	  return nomFor;
	}
  }

}

class ItemNotFoundException extends RuntimeException {

}

class MalformedRequestException extends RuntimeException {

}

@ControllerAdvice
class RestExceptionProcessor {

  @ExceptionHandler(ItemNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ResponseBody
  public void itemNotFound(HttpServletRequest req, ItemNotFoundException ex) {
  }

  @ExceptionHandler(MalformedRequestException.class)
  @ResponseStatus(value = HttpStatus.BAD_GATEWAY)
  @ResponseBody
  public void itemNotFound(HttpServletRequest req, MalformedRequestException ex) {
  }

}
