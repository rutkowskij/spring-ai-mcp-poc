// File: person-mcp-service/src/test/java/pl/piomin/services/PersonMCPServerTests.java
package pl.piomin.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = PersonMCPServer.class)
@ActiveProfiles("test")
class PersonMCPServerTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring Boot application context for PersonMCPServer starts successfully
    }
}
</newLines>
<newLines>
# File: person-mcp-service/src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb-person
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

management:
  endpoints:
    web:
      exposure:
        include: health,info

mcp:
  server:
    enabled: false

server:
  port: 0