# SpringBoot Demo
A PoC of SpringBoot project using RESTful

## Usage
To start up the SpringBoot project
```
java -jar ${project}.jar --spring.config.location=classpath:/demo.yml
```

To query the RESTful endpoint
```
curl ${url}
```

## Exposed URI

### demo.jar
Demontrating microservice to read value from YML properties
```
http://localhost:8080/
http://localhost:8080/listFromValue
http://localhost:8080/listFromYML
```

### parent.jar
Demontrating microservice with synchronized and asynchronized feature
```
http://localhost:8081/parent/blocking
http://localhost:8081/parent/nonBlocking
http://localhost:8081/parent/nonBlockingMultiple
```
Demontrating microservice to invoke child microservices of different type asynchronously
```
http://localhost:8081/parent/string
http://localhost:8081/parent/map
http://localhost:8081/parent/pojo
http://localhost:8081/parent/jsonString
http://localhost:8081/parent/jsonObject
http://localhost:8081/parent/bytes
http://localhost:8081/parent/generic
```

### child.jar
Demontrating microservice with an artificial sleep to simulate blocking call
```
http://localhost:8082/child/blocking
```

### pojo.jar
Demontrating microservice with POJO response
```
http://localhost:8083/type/map
http://localhost:8083/type/pojo
```

### json.jar
Demontrating microservice with JSON response
```
http://localhost:8084/type/string
http://localhost:8084/type/jsonString
http://localhost:8084/type/jsonStringInteger
http://localhost:8084/type/jsonObject
```

### byte.jar
Demontrating microservice with byte array response
```
http://localhost:8085/type/bytes
http://localhost:8085/type/list
http://localhost:8085/type/encodedBytes
```

### scheduler.jar
Demontrating microservice with dependency scheduling run
```
http://localhost:8086/schedule/load
```