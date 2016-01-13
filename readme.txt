This is a spring.io JPA Rest service that is configured to run under Spring Boot with embedded Tomcat for a service container. The service is configured by default to use an HSQLDB database named "nomdb" and will create itself on startup if needed. It is configured to deploy on port 7293. These values can be changed by modifying the application.properties file.
The maven build script will create an executable jar named "nominationService-{version}.jar" in the /target directory. To start the service, issue the following command:
java -jar nominationService-0.1.0.jar

The service registers the following endpoints:

/care-plans/{carePlanId} (GET) -> returns a JSON representation of a list of ChangeRequest objects for a given CarePlan, along with its associated nominations. This is a virtual object that is built from nomination objects saved in the database and is not directly represented in the database.
/care-plans/{carePlanId}/authors (GET) -> returns a JSON representation of all authors of ChangeRequests for a given CarePlan
/care-plans/{carePlanId}/authors/{authorId} (GET) -> returns a JSON representation of a ChangeRequest object for a given CarePlan and Author, along with its associated nominations. This is a virtual object that is built from nomination objects saved in the database and is not directly represented in the database.
/care-plans/{carePlanId}/authors/{authorId}/{resourceType} (GET) -> returns a JSON collection of nominations of {resourceType}, an arbitrary string
/care-plans/{carePlanId}/authors/{authorId}/{resourceType}/{resourceId} (PUT) -> creates a nomination from a JSON object of type {resourceType}, an arbitrary string
/care-plans/{carePlanId}/authors/{authorId}/resources/{resourceId} (GET) -> returns a JSON representation of a nomination object
/care-plans/{carePlanId}/authors/{authorId}/resources/{resourceId} (DELETE) -> delete a specific nomination object

Nominations have the following structure on input:
{
	"action": <String>,
	"existing": <JSON object>,
	"proposed": <JSON object>
}

Nominations have the following structure on output:
{
	"carePlanId": <String>,
	"authorId": <String>,
	"resourceId": <String>,
	"action": <String>,
	"existing": <JSON object>,
	"proposed": <JSON object>,
	"diff": <Generated RFC 6902 JSON array>
}

The CarePlan id is an arbitrary string. The nominationType can be any arbitrary string, but the ChangeRequest serialization will only include nominations of the following types:"conditions", "nutrition-orders", "goals", "medication-orders", and "procedure-requests". The contents of "existing", "proposed", and "diff" are configured to have a 64k limit in length. Modifying these values requires recompilation.

The nomination object takes arbitrary JSON objects for the values of the proposed and existing attributes. When both objects are set during insertion, an RFC 6902 (https://tools.ietf.org/html/rfc6902) diff is generated and inserted into the diff attribute.
