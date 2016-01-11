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
@RequestMapping("/patients")
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

    @RequestMapping(value = "/{patientId}", method = RequestMethod.GET)
    List<ChangeRequest> getChangeRequestList(@PathVariable String patientId) {
        List<ChangeRequest> changeRequests = new ArrayList<>();

        List<ChangeRequestAuthor> authors = findChangeRequestAuthors(patientId);
        for (ChangeRequestAuthor author : authors) {
            ChangeRequest changeRequest = findChangeRequest(patientId, author.getAuthorId());
            changeRequests.add(changeRequest);
        }

        return changeRequests;
    }

    @RequestMapping(value = "/{patientId}/authors", method = RequestMethod.GET)
    List<ChangeRequestAuthor> getChangeRequestAuthorList(@PathVariable String patientId) {
        return findChangeRequestAuthors(patientId);
    }

    @RequestMapping(value = "/{patientId}/authors/{authorId}", method = RequestMethod.GET)
    ChangeRequest getChangeRequest(@PathVariable String patientId, @PathVariable String authorId) {
        return findChangeRequest(patientId, authorId);
    }

    @RequestMapping(value = "/{patientId}/authors/all/{resourceType}", method = RequestMethod.GET)
    List<Nomination> getNominationListForAllAuthors(@PathVariable String patientId, @PathVariable String resourceType) {
        resourceType = singularize(resourceType);
        return nominationRepo.findByPatientIdAndResourceType(patientId, resourceType);
    }

    @RequestMapping(value = "/{patientId}/authors/{authorId}/{resourceType}", method = RequestMethod.GET)
    List<Nomination> getNominationListForAuthor(@PathVariable String patientId, @PathVariable String authorId, @PathVariable String resourceType) {
        resourceType = singularize(resourceType);
        return nominationRepo.findByPatientIdAndAuthorIdAndResourceType(patientId, authorId, resourceType);
    }

    @RequestMapping(value = "/{patientId}/authors/{authorId}/{resourceType}/{resourceId}", method = RequestMethod.PUT)
    ResponseEntity<?> createNomination(
            @PathVariable String patientId, @PathVariable String authorId, @PathVariable String resourceType,
            @PathVariable String resourceId, @RequestBody Nomination input) {
        resourceType = singularize(resourceType);

        Nomination nomination = new Nomination(patientId, authorId, resourceId, input.getAction(), resourceType,
                input.getExisting(), input.getProposed(), input.getDiff());
        nominationRepo.save(nomination);

        // TODO: return HttpStatus.NO_CONTENT if the resource was updated
        return new ResponseEntity<>(null, null, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{patientId}/authors/all/resources/{resourceId}", method = RequestMethod.GET)
    List<Nomination> getNominationList(@PathVariable String patientId, @PathVariable String resourceId) {
        // finds all nominations for a given resource (returns results from multiple authors
        return nominationRepo.findByPatientIdAndResourceId(patientId, resourceId);
    }

    @RequestMapping(value = "/{patientId}/authors/{authorId}/resources/{resourceId}", method = RequestMethod.GET)
    Nomination getNomination(
            @PathVariable String patientId, @PathVariable String authorId, @PathVariable String resourceId) {

        Nomination nomination = nominationRepo.findByPatientIdAndAuthorIdAndResourceId(
                patientId, authorId, resourceId);
        if (nomination == null) {
            throw new ItemNotFoundException();
        } else {
            return nomination;
        }
    }

    @RequestMapping(value = "/{patientId}/authors/{authorId}/resources/{resourceId}", method = RequestMethod.DELETE)
    void deleteNomination(
            @PathVariable String patientId, @PathVariable String authorId, @PathVariable String resourceId) {

        Nomination nomination = nominationRepo.findByPatientIdAndAuthorIdAndResourceId(
                patientId, authorId, resourceId);

        if (nomination != null) {
            nominationRepo.delete(nomination);
        } else {
            throw new ItemNotFoundException();
        }
    }

    private List<ChangeRequestAuthor> findChangeRequestAuthors(String patientId) {
        List<Object[]> results = nominationRepo.findAuthorIdsByPatientId(patientId);
        List<ChangeRequestAuthor> value = new ArrayList<>();
        value.addAll(results.stream().map(result -> new ChangeRequestAuthor((String) result[0], (Date) result[1]))
                .collect(Collectors.toList()));
        return value;
    }

    private ChangeRequest findChangeRequest(String patientId, String authorId) {
        List<Nomination> conditions = nominationRepo.findByPatientIdAndAuthorIdAndResourceType(patientId, authorId, RES_PROBLEM);
        List<Nomination> goals = nominationRepo.findByPatientIdAndAuthorIdAndResourceType(patientId, authorId, RES_GOAL);
        List<Nomination> medOrders = nominationRepo.findByPatientIdAndAuthorIdAndResourceType(patientId, authorId, RES_MEDICATION);
        List<Nomination> nutritionOrders = nominationRepo.findByPatientIdAndAuthorIdAndResourceType(patientId, authorId, RES_NUTRITION);
        List<Nomination> procedureRequests = nominationRepo.findByPatientIdAndAuthorIdAndResourceType(patientId, authorId, RES_INTERVENTION);

        // find the most recent timestamp of this change request
        List<Nomination> all = Stream.of(conditions, goals, medOrders, nutritionOrders, procedureRequests)
                .flatMap(Collection::stream).collect(Collectors.toList());
        if (all.size() == 0) {
            // there are no Nominations for this Patient/Author, therefore there is no ChangeRequest
            throw new ItemNotFoundException();
        }
        Nomination newest = Collections.max(all, Nomination.TimestampComparator);

        return new ChangeRequest(patientId, authorId, newest.getTimestamp(), conditions, goals, medOrders,  nutritionOrders, procedureRequests);
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
