/*
 * Copyright 2016 The MITRE Corporation, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package careshare.nominationService.controller;

import careshare.nominationService.model.ChangeRequest;
import careshare.nominationService.model.ChangeRequestAuthor;
import careshare.nominationService.model.Nomination;
import careshare.nominationService.model.NominationList;
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
@RequestMapping("/")
class ChangeRequestController {

    private final NominationRepo nominationRepo;

    private static final String RES_PROBLEM = "condition";
    private static final String RES_GOAL = "goal";
    private static final String RES_INTERVENTION = "procedure-request";
    private static final String RES_NUTRITION = "nutrition-order";
//    private static final String RES_MEDICATION = "medication-order";

    @Autowired
    ChangeRequestController(NominationRepo nominationRepo) {
        this.nominationRepo = nominationRepo;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CHANGE REQUESTS

    @RequestMapping(value = "change-requests/patient-id/{patientId:.*}", method = RequestMethod.GET)
    List<ChangeRequest> getPatientChangeRequestList(@PathVariable String patientId) {
        List<ChangeRequest> changeRequests = new ArrayList<>();

        List<String> carePlanIds = findChangeRequestCarePlans(patientId);
        for (String carePlanId : carePlanIds) {
            List<ChangeRequest> result = findChangeRequestList(carePlanId);
            changeRequests.addAll(result);
        }

        return changeRequests;
    }

    @RequestMapping(value = "change-requests/care-plan-id/{carePlanId:.*}", method = RequestMethod.GET)
    List<ChangeRequest> getCarePlanChangeRequestList(@PathVariable String carePlanId) {
        return findChangeRequestList(carePlanId);
    }

    @RequestMapping(value = "change-requests/care-plan-id/{carePlanId}/author-id/{authorId:.*}", method = RequestMethod.GET)
    ChangeRequest getChangeRequest(@PathVariable String carePlanId, @PathVariable String authorId) {
        return findChangeRequest(carePlanId, authorId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // AUTHORS

    @RequestMapping(value = "authors/care-plan-id/{carePlanId:.*}", method = RequestMethod.GET)
    List<ChangeRequestAuthor> getChangeRequestAuthorList(@PathVariable String carePlanId) {
        return findChangeRequestAuthors(carePlanId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NOMINATIONS

    @RequestMapping(value = "nominations/care-plan-id/{carePlanId}/resource-type/{resourceType:.*}", method = RequestMethod.GET)
    List<Nomination> getNominationsForCarePlanIdAndResourceType(@PathVariable String carePlanId, @PathVariable String resourceType) {
        // for all authors of nominations that refer to this CarePlan, filter by resource type
        return nominationRepo.findByCarePlanIdAndResourceType(carePlanId, resourceType);
    }

    @RequestMapping(value = "nominations/care-plan-id/{carePlanId}/author-id/{authorId}/resource-type/{resourceType:.*}", method = RequestMethod.GET)
    List<Nomination> getNominationsForCarePlanIdAndAuthorIdAndResourceType(
            @PathVariable String carePlanId, @PathVariable String authorId, @PathVariable String resourceType) {
        // for a single author of nominations that refer to this CarePlan, filter by resource type
        return nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, resourceType);
    }

    @RequestMapping(value = "nominations/patient-id/{patientId}/resource-type/{resourceType:.*}", method = RequestMethod.GET)
    List<Nomination> getNominationsForPatientIdAndResourceType(
            @PathVariable String patientId, @PathVariable String resourceType) {
        // for a single author of nominations that refer to this Patient, filter by resource type
        return nominationRepo.findByPatientIdAndResourceType(patientId, resourceType);
    }

    @RequestMapping(value = "nominations/patient-id/{patientId}/author-id/{authorId}/resource-type/{resourceType:.*}", method = RequestMethod.GET)
    List<Nomination> getNominationsForPatientIdAndAuthorIdAndResourceType(
            @PathVariable String patientId, @PathVariable String authorId, @PathVariable String resourceType) {
        // for a single author of nominations that refer to this Patient, filter by resource type
        return nominationRepo.findByPatientIdAndAuthorIdAndResourceType(patientId, authorId, resourceType);
    }

    @RequestMapping(value = "nominations/resource-id/{resourceId:.*}", method = RequestMethod.GET)
    List<Nomination> getNominationsForResourceId(@PathVariable String resourceId) {
        // for all authors of nominations that refer to this Resource
        return nominationRepo.findByResourceId(resourceId);
    }

    @RequestMapping(value = "nominations/author-id/{authorId}/resource-id/{resourceId:.*}", method = RequestMethod.GET)
    List<Nomination> getNominationForAuthorIdAndResourceId(@PathVariable String authorId, @PathVariable String resourceId) {
        return nominationRepo.findByAuthorIdAndResourceId(authorId, resourceId);
    }

    // returns 'true' if there are any nominations for the given patient
    @RequestMapping(value = "nominations/patient-ids/{patientIds:.*}", method = RequestMethod.GET)
    Map<String, Boolean> getNominationsForPatientId(@PathVariable String[] patientIds) {
        return getPatientChangeMap(patientIds, null);
    }

    // returns 'true' if there are any nominations for the given patient and author
    @RequestMapping(value = "nominations/author-id/{authorId}/patient-ids/{patientIds:.*}", method = RequestMethod.GET)
    Map<String, Boolean> getNominationForAuthorIdAndPatientId(@PathVariable String authorId, @PathVariable String[] patientIds) {
        return getPatientChangeMap(patientIds, authorId);
    }

    @RequestMapping(value = "nominations", method = RequestMethod.PUT)
    ResponseEntity<?> createNomination(@RequestBody NominationList input) {
        if (input.size() == 0) {
            throw new MalformedRequestException();
        }

        // delete any existing nominations for this author/resource
        Nomination first = input.get(0);
        List<Nomination> existing = nominationRepo.findByAuthorIdAndResourceId(first.getAuthorId(), first.getResourceId());
        existing.forEach(nominationRepo::delete);

        // save the new nomination(s)
        input.forEach(nominationRepo::save);

        return new ResponseEntity<>(null, null, HttpStatus.CREATED); // 201
    }

    @RequestMapping(value = "nominations/id/{id:.*}", method = RequestMethod.DELETE)
    void deleteNomination(@PathVariable Long id) {
        // each author can only have one nomination, so the carePlanId is not needed
        Nomination nomination = nominationRepo.findById(id);

        if (nomination != null) {
            nominationRepo.delete(nomination);
        } else {
            throw new ItemNotFoundException();
        }
    }

    @RequestMapping(value = "nominations/resource-id/{resourceId:.*}", method = RequestMethod.DELETE)
    void deleteAllNominationsForResource(@PathVariable String resourceId) {

        List<Nomination> nominations = nominationRepo.findByResourceId(resourceId);

        // NOTE: this will delete ALL nominations for this resourceId (not just those that have a 'delete' action)
        nominations.forEach(nominationRepo::delete);
        // should we return 404 if no nominations exist?
    }

    @RequestMapping(value = "nominations/author-id/{authorId}/resource-id/{resourceId:.*}", method = RequestMethod.DELETE)
    void deleteAllNominationsForAuthorAndResource(@PathVariable String authorId, @PathVariable String resourceId) {

        List<Nomination> nominations = nominationRepo.findByAuthorIdAndResourceId(authorId, resourceId);

        // NOTE: this will delete ALL nominations for this resourceId (not just those that have a 'delete' action)
        nominations.forEach(nominationRepo::delete);
        // should we return 404 if no nominations exist?
    }

    private List<String> findChangeRequestCarePlans(String patientId) {
        return nominationRepo.findCarePlanIdsByPatientId(patientId);
    }

    private List<ChangeRequestAuthor> findChangeRequestAuthors(String carePlanId) {
        List<Object[]> results = nominationRepo.findAuthorIdsByCarePlanId(carePlanId);
        List<ChangeRequestAuthor> value = new ArrayList<>();
        value.addAll(results.stream().map(result -> new ChangeRequestAuthor((String) result[0], (Date) result[1]))
                .collect(Collectors.toList()));
        return value;
    }

    private List<ChangeRequest> findChangeRequestList(String carePlanId) {
        List<ChangeRequest> changeRequests = new ArrayList<>();

        List<ChangeRequestAuthor> authors = findChangeRequestAuthors(carePlanId);
        for (ChangeRequestAuthor author : authors) {
            ChangeRequest changeRequest = findChangeRequest(carePlanId, author.getAuthorId());
            changeRequests.add(changeRequest);
        }

        return changeRequests;
    }

    private ChangeRequest findChangeRequest(String carePlanId, String authorId) {
        List<Nomination> conditions = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_PROBLEM);
        List<Nomination> goals = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_GOAL);
        List<Nomination> procedureRequests = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_INTERVENTION);
        List<Nomination> nutritionOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_NUTRITION);
//        List<Nomination> medOrders = nominationRepo.findByCarePlanIdAndAuthorIdAndResourceType(carePlanId, authorId, RES_MEDICATION);

        // find the most recent timestamp of this change request
//        List<Nomination> all = Stream.of(conditions, goals, procedureRequests, nutritionOrders, medOrders)
        List<Nomination> all = Stream.of(goals, procedureRequests, nutritionOrders)
                .flatMap(Collection::stream).collect(Collectors.toList());
        if (all.size() == 0) {
            // there are no Nominations for this CarePlan/Author, therefore there is no ChangeRequest
            throw new ItemNotFoundException();
        }
        Nomination newest = Collections.max(all, Nomination.TimestampComparator);

//        return new ChangeRequest(carePlanId, authorId, newest.getTimestamp(), conditions, goals, procedureRequests, nutritionOrders, medOrders);
        return new ChangeRequest(carePlanId, authorId, newest.getPatientId(), newest.getTimestamp(), conditions, goals, procedureRequests, nutritionOrders);
    }

    private Map<String, Boolean> getPatientChangeMap(String[] patientIds, String authorId) {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String patientId : patientIds) {
            List<Nomination> nominations;
            if (authorId != null) {
                nominations = nominationRepo.findByPatientIdAndAuthorId(patientId, authorId);
            } else {
                nominations = nominationRepo.findByPatientId(patientId);
            }
            boolean found = nominations.size() > 0;
            map.put(patientId, found);
            for (Nomination nomination : nominations) {
                map.put(nomination.getCarePlanId(), true);
            }
        }
        return map;
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
