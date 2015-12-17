This is a spring.io JPA Rest service that is configured to run under Spring Boot with embedded Tomcat for a service container. The service is configured by default to use an HSQLDB database named "nomdb" and will create itself on startup if needed. It is configured to deploy on port 7293. These values can be changed by modifying the application.properties file. 
The maven build script will create an executable jar named "nominationService-{version}.jar" in the /target directory. To start the service, issue the following command:
java -jar nominationService-0.1.0.jar


The service registers the following endpoints:

/careplan/{id} (GET) -> returns a JSON representation of a CarePlan object and its associated nominations. This is a virtual object that is built from nomination objects saved in the database and is not directly represented in the database.
/careplan/{id}/{nominationType} (PUT) -> creates a nomination from a JSON object
/careplan/{id}/{nominationType}/{id} (GET) -> returns a JSON representation of a nomination object. 
/careplan/{id}/{nominationType}/{id} (DELETE) -> delete a specific nomination
/careplan/{id}/{nominationType} (GET) -> returns a JSON collection of nominations of {nominationType}, an arbitrary string. 


Nominations have the following structure on input:
{
	"action": <String>,
	"existing": <JSON object>,
	"proposed": <JSON object>
}

Nominations have the following structure on output:
{
	"id": <Long>,
	"careplan": <String>,
	"action": <String>,
	"existing": <JSON object>,
	"proposed": <JSON object>,
	"diff": <Generated RFC 6902 JSON array>
}


The careplan id is an arbitrary string. The nominationType can be any arbitrary string, but the careplan serialization will only include nominations of the following types:"conditions", "diagnostic-orders", "goals", "medication-orders", and "procedure-requests". The contents of "existing", "proposed", and "diff" are configured to have a 64k limit in length. Modifying these values requires recompilation. 

The nomination object takes arbitrary JSON objects for the values of the proposed and existing attributes. When both objects are set during insertion, an RFC 6902 (https://tools.ietf.org/html/rfc6902) diff is generated and inserted into the diff attribute. 

For the examples that follow, the nomination service was running on localhost, configured to listen on port 8080.

An example of how to insert a nomination from the curl utility:
curl -i -X PUT -H "Content-Type:application/json" -d '{ "action": "update", "proposed": {}, "existing": {"prop": "value2", "prop2": 1}}' http://localhost:8080/careplan/17/procedure-requests/

Response:
HTTP/1.1 201 Created
Server: Apache-Coyote/1.1
Location: http://localhost:8080/careplan/17/procedure-requests/18
Content-Length: 0
Date: Thu, 17 Dec 2015 03:02:04 GMT


Reading the nomination:
curl -i -X GET -H "Content-Type:application/json"  http://localhost:8080/careplan/17/procedure-requests/18

Response:
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Thu, 17 Dec 2015 03:05:25 GMT

{"id":18,"careplan":"17","action":"update","nominationFor":"procedure-request","existing":{"prop":"value2","prop2":1},"proposed":{},"diff":[{"op":"remove","path":"/prop"},{"op":"remove","path":"/prop2"}]}

Reading all procedure-request nominations for careplan “17”
curl -i -X GET -H "Content-Type:application/json"  http://localhost:8080/careplan/17/procedure-requests/

Response:
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Thu, 17 Dec 2015 03:05:25 GMT

[{"id":18,"careplan":"17","action":"update","nominationFor":"procedure-request","existing":{"prop":"value2","prop2":1},"proposed":{},"diff":[{"op":"remove","path":"/prop"},{"op":"remove","path":"/prop2"}]}]


Reading all nominations for careplan “17”:
curl -i -X GET -H "Content-Type:application/json"  http://localhost:8080/careplan/17

Response:
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Thu, 17 Dec 2015 03:20:54 GMT

{"id":"17","conditions":[],"diagnosticOrders":[],"goals":[],"medicationOrders":[],"procedureRequests":[{"id":18,"careplan":"17","action":"update","existing":{"prop":"value2","prop2":1},"proposed":{},"diff":[{"op":"remove","path":"/prop"},{"op":"remove","path":"/prop2"}]}]}
