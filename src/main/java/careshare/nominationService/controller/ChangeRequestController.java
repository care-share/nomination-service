package careshare.nominationService.controller;

import careshare.nominationService.model.ChangeRequest;
import careshare.nominationService.model.Nomination;
import careshare.nominationService.repo.NominationRepo;

import java.util.ArrayList;
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
    Collection<ChangeRequest> getChangeRequestList(@PathVariable String carePlanId) {
        Collection<ChangeRequest> changeRequests = new ArrayList<>();

        Collection<String> authorIds = nominationRepo.findAuthorIdsByCarePlanId(carePlanId);
        for (String authorId : authorIds) {
            ChangeRequest changeRequest = findChangeRequest(carePlanId, authorId);
            changeRequests.add(changeRequest);
        }

        return changeRequests;
    }

    @RequestMapping(value = "/{carePlanId}/authors", method = RequestMethod.GET)
    Collection<String> getChangeRequestAuthorList(@PathVariable String carePlanId) {
        return nominationRepo.findAuthorIdsByCarePlanId(carePlanId);
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}", method = RequestMethod.GET)
    ChangeRequest getChangeRequest(@PathVariable String carePlanId, @PathVariable String authorId) {
        return findChangeRequest(carePlanId, authorId);
    }

    @RequestMapping("/{carePlanId}/authors/{authorId}/{resourceType}")
    Collection<Nomination> getNominationList(@PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType) {
        resourceType = singularize(resourceType);
        return nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, resourceType);
    }

    // TODO: change this so we can PUT a nomination and set its ID (will allow us to update/overwrite nominations)
    @RequestMapping(value = "/{carePlanId}/authors/{authorId}/{resourceType}", method = RequestMethod.POST)
    ResponseEntity<?> createNomination(@PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType, @RequestBody Nomination input) {
        resourceType = singularize(resourceType);

        Nomination nomination = new Nomination(carePlanId, authorId, input.getAction(), resourceType,
                input.getExisting(), input.getProposed(), input.getDiff());
        nomination = nominationRepo.save(nomination);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(nomination.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}/{resourceType}/{nominationId}", method = RequestMethod.GET)
    Nomination getNomination(@PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType, @PathVariable Long nominationId) {
        resourceType = singularize(resourceType);

        Nomination nomination = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceTypeAndId(carePlanId, authorId, resourceType, nominationId);
        if (nomination == null) {
            throw new ItemNotFoundException();
        } else {
            return nomination;
        }
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}/{resourceType}/{nominationId}",
            method = RequestMethod.DELETE)
    void deleteNomination(@PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType,
                          @PathVariable Long nominationId) {
        resourceType = singularize(resourceType);

        Nomination nomination = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceTypeAndId(carePlanId, authorId, resourceType, nominationId);

        if (nomination != null) {
            nominationRepo.delete(nomination);
        } else {
            throw new ItemNotFoundException();
        }
    }

    private ChangeRequest findChangeRequest(String carePlanId, String authorId) {
        Collection<Nomination> conditions = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_CONDITION);
        Collection<Nomination> goals = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_GOAL);
        Collection<Nomination> medOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_MED_ORDER);
        Collection<Nomination> nutritionOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_NUTR_ORDER);
        Collection<Nomination> procedureRequests = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_PROC_REQUEST);

        return new ChangeRequest(carePlanId, authorId, conditions, nutritionOrders, goals, medOrders, procedureRequests);
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
