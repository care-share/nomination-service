package careshare.nominationService.controller;

import careshare.nominationService.model.ChangeRequest;
import careshare.nominationService.model.ChangeRequestAuthor;
import careshare.nominationService.model.Nomination;
import careshare.nominationService.repo.NominationRepo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/change-requests")
class ChangeRequestController {

    private final NominationRepo nominationRepo;

    private static final String RES_PROBLEM = "condition";
    private static final String RES_GOAL = "goal";
    private static final String RES_MEDICATION = "medication-order";
    private static final String RES_NUTRITION = "nutrition-order";
    private static final String RES_INTERVENTION = "procedure-request";

    @Autowired
    ChangeRequestController(NominationRepo nominationRepo) {
        this.nominationRepo = nominationRepo;
    }

    @RequestMapping(value = "/{carePlanId}", method = RequestMethod.GET)
    List<ChangeRequest> getChangeRequestList(@PathVariable String carePlanId) {
        List<ChangeRequest> changeRequests = new ArrayList<>();

        List<ChangeRequestAuthor> authors = findChangeRequestAuthors(carePlanId);
        for (ChangeRequestAuthor author : authors) {
            ChangeRequest changeRequest = findChangeRequest(carePlanId, author.getAuthorId());
            changeRequests.add(changeRequest);
        }

        return changeRequests;
    }

    @RequestMapping(value = "/{carePlanId}/authors", method = RequestMethod.GET)
    List<ChangeRequestAuthor> getChangeRequestAuthorList(@PathVariable String carePlanId) {
        return findChangeRequestAuthors(carePlanId);
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}", method = RequestMethod.GET)
    ChangeRequest getChangeRequest(@PathVariable String carePlanId, @PathVariable String authorId) {
        return findChangeRequest(carePlanId, authorId);
    }

    @RequestMapping(value = "/{carePlanId}/authors/all/{resourceType}", method = RequestMethod.GET)
    List<Nomination> getNominationListForAllAuthors(@PathVariable String carePlanId, @PathVariable String resourceType) {
        resourceType = singularize(resourceType);
        return nominationRepo.findByCarePlanIdAndResourceType(carePlanId, resourceType);
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}/{resourceType}", method = RequestMethod.GET)
    List<Nomination> getNominationListForAuthor(@PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType) {
        resourceType = singularize(resourceType);
        return nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, resourceType);
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}/{resourceType}/{resourceId}", method = RequestMethod.PUT)
    ResponseEntity<?> createNomination(
            @PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType,
            @PathVariable String resourceId, @RequestBody Nomination input) {
        resourceType = singularize(resourceType);

        Nomination nomination = new Nomination(carePlanId, authorId, resourceId, input.getAction(), resourceType,
                input.getExisting(), input.getProposed(), input.getDiff());
        nominationRepo.save(nomination);

        // TODO: return HttpStatus.NO_CONTENT if the resource was updated
        return new ResponseEntity<>(null, null, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{carePlanId}/authors/all/resources/{resourceId}", method = RequestMethod.GET)
    List<Nomination> getNominationList(@PathVariable String carePlanId, @PathVariable String resourceId) {
        // finds all nominations for a given resource (returns results from multiple authors
        return nominationRepo.findByCarePlanIdAndResourceId(carePlanId, resourceId);
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}/resources/{resourceId}", method = RequestMethod.GET)
    Nomination getNomination(
            @PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceId) {

        Nomination nomination = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceId(
                carePlanId, authorId, resourceId);
        if (nomination == null) {
            throw new ItemNotFoundException();
        } else {
            return nomination;
        }
    }

    @RequestMapping(value = "/{carePlanId}/authors/{authorId}/resources/{resourceId}", method = RequestMethod.DELETE)
    void deleteNomination(
            @PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceId) {

        Nomination nomination = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceId(
                carePlanId, authorId, resourceId);

        if (nomination != null) {
            nominationRepo.delete(nomination);
        } else {
            throw new ItemNotFoundException();
        }
    }

    private List<ChangeRequestAuthor> findChangeRequestAuthors(String carePlanId) {
        List<Object[]> results = nominationRepo.findAuthorIdsByCarePlanId(carePlanId);
        List<ChangeRequestAuthor> value = new ArrayList<>();
        value.addAll(results.stream().map(result -> new ChangeRequestAuthor((String) result[0], (Date) result[1]))
                .collect(Collectors.toList()));
        return value;
    }

    private ChangeRequest findChangeRequest(String carePlanId, String authorId) {
        List<Nomination> conditions = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_PROBLEM);
        List<Nomination> goals = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_GOAL);
        List<Nomination> medOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_MEDICATION);
        List<Nomination> nutritionOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_NUTRITION);
        List<Nomination> procedureRequests = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_INTERVENTION);

        // find the most recent timestamp of this change request
        List<Nomination> all = Stream.of(conditions, goals, medOrders, nutritionOrders, procedureRequests)
                .flatMap(Collection::stream).collect(Collectors.toList());
        if (all.size() == 0) {
            // there are no Nominations for this CarePlan/Author, therefore there is no ChangeRequest
            throw new ItemNotFoundException();
        }
        Nomination newest = Collections.max(all, Nomination.TimestampComparator);

        return new ChangeRequest(carePlanId, authorId, newest.getTimestamp(), conditions, goals, medOrders,  nutritionOrders, procedureRequests);
    }

    private String singularize(String resourceType) {
        String value;
        if (resourceType.endsWith("s")) {
            value = resourceType.substring(0, resourceType.length() - 1);
        } else {
            value = resourceType;
        }

        // validate the resource type
        if (!RES_PROBLEM.equals(value) && !RES_GOAL.equals(value) && !RES_MEDICATION.equals(value)
                && !RES_NUTRITION.equals(value) && !RES_INTERVENTION.equals(value)) {
            throw new ItemNotFoundException();
        }

        return value;
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
