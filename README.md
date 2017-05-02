[Usage]  <br />
java -jar ${project}.jar --spring.config.location=classpath:/demo.yml  <br />
  <br />
[Exposed URI]  <br />
demo.jar  <br />
- Demontrating microservice to read value from YML properties  <br />
	http://localhost:8080/  <br />
	http://localhost:8080/listFromValue  <br />
	http://localhost:8080/listFromYML  <br />
  <br />
parent.jar  <br />
- Demontrating microservice with synchronized and asynchronized feature  <br />
	http://localhost:8081/parent/blocking  <br />
	http://localhost:8081/parent/nonBlocking  <br />
	http://localhost:8081/parent/nonBlockingMultiple  <br />
- Demontrating microservice to invoke child microservices of different type asynchronously  <br />
	http://localhost:8081/parent/string  <br />
	http://localhost:8081/parent/map  <br />
	http://localhost:8081/parent/pojo  <br />
	http://localhost:8081/parent/jsonString  <br />
	http://localhost:8081/parent/jsonObject  <br />
	http://localhost:8081/parent/bytes  <br />
	http://localhost:8081/parent/generic  <br />
  <br />
child.jar  <br />
- Demontrating microservice with an artificial sleep to simulate blocking call  <br />
	http://localhost:8082/child/blocking  <br />
  <br />
pojo.jar  <br />
- Demontrating microservice with POJO response  <br />
	http://localhost:8083/type/map  <br />
	http://localhost:8083/type/pojo  <br />
  <br />
json.jar  <br />
- Demontrating microservice with JSON response  <br />
	http://localhost:8084/type/string  <br />
	http://localhost:8084/type/jsonString  <br />
	http://localhost:8084/type/jsonStringInteger  <br />
	http://localhost:8084/type/jsonObject  <br />
  <br />
byte.jar  <br />
- Demontrating microservice with byte array response  <br />
	http://localhost:8085/type/bytes  <br />
	http://localhost:8085/type/list  <br />
	http://localhost:8085/type/encodedBytes  <br />
