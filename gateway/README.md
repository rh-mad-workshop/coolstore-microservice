
== Package an App

```
mvn package spring-boot:repackage 
```

== Build an Container Image

```
docker build -t quay.io/openshiftlabs/coolstore-microservice-gateway:1.0 -f Dockerfile . --platform linux/amd64
```
