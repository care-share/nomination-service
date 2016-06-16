# Careshare Nomination Service

This is a spring.io JPA Rest service that is configured to run under Spring Boot with embedded Tomcat for a service container.

## Prerequisites

You will need the following installed:

* Java 8
* Maven 3.x
* Docker (optional)
* Docker Compose (optional)

## Building

To build the application, open a terminal in the project directory, and run the following command:

```
$ mvn install
```

Maven will automatically download Java dependencies and build the application.
The resulting file will appear at `target/nominationService.jar`.

## Running

To manually start the service, open a terminal in the project directory, and run the following command:

```
$ java -jar target/nominationService.jar
```

Alternatively, you can start the service in a Docker container daemon like so:

```
$ docker-compose build
$ docker-compose up -d
```

Once you start the service, its APIs will be accessible at `http://localhost:7293`.

## Database Backups

To create the backups:

1. Make sure the service is **not** running
2. Copy the files from `db/` to `db/backup/` (which is version-controlled)
3. Optionally, use Git to create a new commit to save the backups to version control

To apply the backups:

1. Make sure the service is **not** running
2. Copy the files from `db/backup/` to `db/` (which is not version-controlled)
3. Run the service

## API Reference

* `GET /change-requests/care-plan-id/:carePlanId`
  * Gets all Change Requests associated with a given CarePlan
  * Returns JSON body:

    ```
    [ { carePlanId: string,
        authorId: string,
        timestamp: date,
        conditions: [ nomination ],
        goals: [ nomination ],
        procedureRequests: [ nomination ],
        nutritionOrders: [ nomination ]
    } ]
    ```

    The `nomination` objects are described further down in this README.

* `GET /change-requests/care-plan-id/:carePlanId/author-id/:authorId`
  * Gets all Change Requests associated with a given CarePlan, filtered to a given Author

* `GET /authors/care-plan-id/:carePlanId
  * Gets a list of Authors who have submitted nominations for a given CarePlan
  * Returns JSON body:

    ```
    [ { authorId: string,
        timestamp: date
    } ]
    ```

* `GET /nominations/care-plan-id/:carePlanId/resource-type/:resourceType`
  * Gets a list of Nominations for a given CarePlan, filtered to a given resource type
  * Valid resource types are: `condition` | `goal` | `procedure-request` | `nutrition-order`
  * Returns JSON body:

    ```
    [ { id: long,
        carePlanId: string,
        authorId: string',
        resourceId: string,
        patientId: string,
        timestamp: date,
        action: string,
        resourceType: string,
        existing: fhirResource,
        proposed: fhirResource
        diff: {
          op: "replace",
          path: "/foo",
          value: "bar",
          originalValue:"baz"
        }
    } ]
    ```

    The `fhirResource` objects are described at the [FHIR documentation pages](https://www.hl7.org/fhir/resourcelist.html).

* `GET /nominations/care-plan-id/:carePlanId/author-id/:authorId/resource-type/:resourceType`
  * Gets a list of Nominations for a given CarePlan, filtered to a given Author and resource type

* `GET /nominations/patient-id/:patientId/resource-type/:resourceType`
  * Gets a list of Nominations for a given Patient, filtered to a given resource type

* `GET /nominations/patient-id/:patientId/author-id/:authorId/resource-type/:resourceType`
  * Gets a list of Nominations for a given Patient, filtered to a given Author and resource type

* `GET /nominations/resource-id/:resourceId`
  * Gets a list of Nominations for a given Patient, filtered to a given Resource

* `GET /nominations/author-id/:authorId/resource-id/:resourceId`
  * Gets a list of Nominations for a given Patient, filtered to a given Author and Resource

* `GET /nominations/patient-ids/:patientIds`
  * Gets a map of whether or not any Nominations exist for a given list of Patients (comma-separated ID values)
  * For example, with a request of "`GET /nominations/patient-ids/foo,bar`", returns JSON body:

    ```
    { foo: true,
      bar: false
    }
    ```

    The keys in the map represent  Patient IDs, and the values represent whether or not any nominations were found for that particular Patient.

* `GET /nominations/author-id/:authorId/patient-ids/:patientIds`
  * Gets a list of Nominations for one or more given Patients (comma-separated), filtered to a given

* `PUT /nominations`
  * Creates a new Nomination from the JSON body of the request:

    ```
    { carePlanId: string,
      authorId: string',
      resourceId: string,
      patientId: string,
      timestamp: date,
      action: string,
      resourceType: string,
      existing: fhirResource,
      proposed: fhirResource
    }
    ```

  * **Note: this will overwrite any exisiting Nominations with the same `authorId` and `resourceId`!** Effectively, one Author may only have one outstanding Nomination for a given Resource (though that Nomination may include multiple changes to various attributes of that Resource).

* `DELETE /nominations/id/:id`
  * Deletes a given Nomination

* `DELETE /nominations/resource-id/:resourceId`
  * Deletes all Nominations for a given Resource

* `DELETE /nominations/author-id/:authorId/resource-id/:resourceId`
  * Deletes all Nominations for a given Author and Resource


## License

Copyright 2016 The MITRE Corporation, All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
