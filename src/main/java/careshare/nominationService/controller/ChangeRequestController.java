package careshare.nominationService.controller;

import careshare.nominationService.model.ChangeRequest;
import careshare.nominationService.model.Nomination;
import careshare.nominationService.repo.NominationRepo;

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

@RestController
@RequestMapping("/change-requests")
class ChangeRequestController {

    private final NominationRepo nominationRepo;

    private static final String NF_CONDITION = "condition";
    private static final String NF_GOAL = "goal";
    private static final String NF_MED_ORDER = "medication-order";
    private static final String NF_NUTR_ORDER = "nutrition-order";
    private static final String NF_PROC_REQUEST = "procedure-request";

    @Autowired
    ChangeRequestController(NominationRepo nominationRepo) {
        this.nominationRepo = nominationRepo;
    }

    @RequestMapping(value = "/{carePlanId}", method = RequestMethod.GET)
    ChangeRequest getChangeRequest(@PathVariable String carePlanId) {
        Collection<Nomination> conditions = nominationRepo.findByCarePlanIdAndResourceType(carePlanId, NF_CONDITION);
        Collection<Nomination> goals = nominationRepo.findByCarePlanIdAndResourceType(carePlanId, NF_GOAL);
        Collection<Nomination> medOrders = nominationRepo.findByCarePlanIdAndResourceType(carePlanId, NF_MED_ORDER);
        Collection<Nomination> nutritionOrders = nominationRepo.findByCarePlanIdAndResourceType(carePlanId, NF_NUTR_ORDER);
        Collection<Nomination> procedureRequests = nominationRepo.findByCarePlanIdAndResourceType(carePlanId, NF_PROC_REQUEST);

        ChangeRequest plan = new ChangeRequest(carePlanId, conditions, nutritionOrders, goals, medOrders, procedureRequests);
        return plan;
    }

    @RequestMapping("/{carePlanId}/{resourceType}")
    Collection<Nomination> getNominationList(@PathVariable String carePlanId, @PathVariable String resourceType) {
        resourceType = singularize(resourceType);
        Collection<Nomination> rings = nominationRepo.findByCarePlanIdAndResourceType(carePlanId, resourceType);
        return rings;
    }

    @RequestMapping(value = "/{carePlanId}/{resourceType}", method = RequestMethod.POST)
    ResponseEntity<?> createNomination(@PathVariable String carePlanId, @PathVariable String resourceType, @RequestBody Nomination input) {
        resourceType = singularize(resourceType);

        Nomination res = new Nomination(carePlanId, input.getAction(), resourceType,
                input.getExisting(), input.getProposed(), input.getDiff());
        res = nominationRepo.save(res);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(res.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{carePlanId}/{resourceType}/{nominationId}", method = RequestMethod.GET)
    Nomination getNomination(@PathVariable String carePlanId, @PathVariable String resourceType, @PathVariable Long nominationId) {
        resourceType = singularize(resourceType);

        Nomination ring = nominationRepo.findByCarePlanIdAndResourceTypeAndId(carePlanId, resourceType, nominationId);
        if (ring == null) {
            throw new ItemNotFoundException();
        } else {
            return ring;
        }
    }

    @RequestMapping(value = "/{carePlanId}/{resourceType}/{nominationId}",
            method = RequestMethod.DELETE)
    void deleteNomination(@PathVariable String carePlanId, @PathVariable String resourceType,
                          @PathVariable Long nominationId) {
        resourceType = singularize(resourceType);

        Nomination ring = nominationRepo.findByCarePlanIdAndResourceTypeAndId(carePlanId, resourceType, nominationId);

        if (ring != null) {
            nominationRepo.delete(ring);
        } else {
            throw new ItemNotFoundException();
        }
    }

    private String singularize(String resourceType) {
        if (resourceType.endsWith("s")) {
            return resourceType.substring(0, resourceType.length() - 1);
        } else {
            return resourceType;
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
