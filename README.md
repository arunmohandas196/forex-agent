# Forex-Agent project

This is the agent that talk with forex-service, and can be scheduled to run for a predefined criteria of configured currencies and exchange date.
ForexAgent uses Spring scheduler to periodically call a forex-service for retrieving forex details configured currencies and exchange date in application.yml.
Currently task is scheduled to run periodically in fixed interval of 60 seconds.
Make change for forex details in application.yml file

## How to Run

* Clone this repository
* Make sure you are using JDK 1.8 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:

```
    mvn spring-boot:run
    
    OR

   java -jar target/forex-agent-0.1.0.jar
```

Currently error handling is a generic catch on error and logging the same on log.
Need to make more changes on this for make the RestTemplate call retryable and support for generic errorResponse and adding metrices and more logs

