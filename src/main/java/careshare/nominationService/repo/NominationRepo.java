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

package careshare.nominationService.repo;

import careshare.nominationService.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NominationRepo extends JpaRepository<Nomination, Long> {
    String FIND_AUTHORS = "SELECT author_id, MAX(timestamp) FROM nomination WHERE care_plan_id = ?1 GROUP BY author_id";
    @Query(value = FIND_AUTHORS, nativeQuery = true)
    List<Object[]> findAuthorIdsByCarePlanId(String carePlanId);

    String FIND_CAREPLANS = "SELECT DISTINCT care_plan_id FROM nomination WHERE patient_id = ?1";
    @Query(value = FIND_CAREPLANS, nativeQuery = true)
    List<String> findCarePlanIdsByPatientId(String patientId);
    // can't auto-magically map a native query to a POJO, need to do it manually in our controller :(

    List<Nomination> findByCarePlanIdAndResourceType(String carePlanId, String resourceType);

    List<Nomination> findByCarePlanIdAndAuthorIdAndResourceType(String carePlanId, String authorId, String resourceType);

    List<Nomination> findByResourceId(String resourceId);

    List<Nomination> findByAuthorIdAndResourceId(String authorId, String resourceId);

    List<Nomination> findByPatientId(String patientId);

    List<Nomination> findByPatientIdAndAuthorId(String patientId, String authorId);

    List<Nomination> findByPatientIdAndResourceType(String patientId, String resourceType);

    List<Nomination> findByPatientIdAndAuthorIdAndResourceType(String patientId, String authorId, String resourceType);

    Nomination findById(Long id);
}
