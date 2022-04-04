Simple Microservice Example
===========================

This is the source code for [_Microservices with Apache Camel, Spring Boot and Docker_](https://platform.deloitte.com.au/articles/2016/microservices-springboot-camel-docker/)
blog post on the Deloitte (former Sixtree) website.

Using Groovy, Gradle, Apache Camel, Spring Boot and Docker, we demonstrate how
to quickly create a microservice.

The microservice exposes a REST API for interacting with an in-memory H2 Database.

To run the application through Spring Boot (not Docker):

```
  $ gradle bootRun
```

NOTE: Docker build and run not working just yet.
To build the Docker image for the project:

```
  $ gradle buildDocker
```

To run the container for the above image:

```
  $ docker run -p 8080:8080 thing-service
```

To test the applications
```

$ curl --request POST \
    --header 'Content-Type: application/json' \
    --data '{"name":"bob", "owner":"someone"}' \
    http://localhost:8080/things

  {"id":1,"name":"bob","owner":"someone"}

$ curl --request POST \
    --header 'Content-Type: application/json' \
    --data '{"name":"chuck", "owner":"someone else"}' \
    http://localhost:8080/things

  {"id":2,"chuck":"bob","owner":"someone else"}

$ curl --request GET 'http://localhost:8080/things' -s | jq .
{
  "size": 2,
  "things": [
    {
      "id": 1,
      "name": "bob",
      "owner": "someone"
    },
    {
      "id": 2,
      "name": "chuck",
      "owner": "someone else"
    }
  ]
}
```
URLs
----

```
https://platform.deloitte.com.au/articles/2016/microservices-springboot-camel-docker/
https://camel.apache.org/releases/release-3.16.0/
```

<!--
vim:ft=markdown:tw=73
-->
