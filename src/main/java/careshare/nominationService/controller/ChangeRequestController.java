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

    @RequestMapping("/{carePlanId}/authors/{authorId}/{resourceType}")
    List<Nomination> getNominationList(@PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType) {
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

    private List<ChangeRequestAuthor> findChangeRequestAuthors(String carePlanId) {
        List<Object[]> results = nominationRepo.findAuthorIdsByCarePlanId(carePlanId);
        List<ChangeRequestAuthor> value = new ArrayList<>();
        value.addAll(results.stream().map(result -> new ChangeRequestAuthor((String) result[0], (Date) result[1]))
                .collect(Collectors.toList()));
        return value;
    }

    private ChangeRequest findChangeRequest(String carePlanId, String authorId) {
        List<Nomination> conditions = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_CONDITION);
        List<Nomination> goals = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_GOAL);
        List<Nomination> medOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_MED_ORDER);
        List<Nomination> nutritionOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_NUTR_ORDER);
        List<Nomination> procedureRequests = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, NF_PROC_REQUEST);

        // find the most recent timestamp of this change request
        List<Nomination> all = Stream.of(conditions, goals, medOrders, nutritionOrders, procedureRequests)
                .flatMap(Collection::stream).collect(Collectors.toList());
        if (all.size() == 0) {
            // there are no Nominations for this CarePlan/Author, therefore there is no ChangeRequest
            throw new ItemNotFoundException();
        }
        Nomination newest = Collections.max(all, Nomination.TimestampComparator);

        return new ChangeRequest(carePlanId, authorId, newest.getTimestamp(), conditions, nutritionOrders, goals, medOrders, procedureRequests);
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
