package pl.piomin.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PersonMCPServerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context (including MCP tools) loads without errors
    }
}
</newLines>

<rationale>
Added a full set of integration tests for PersonTools in PersonToolsIntegrationTest.java.  We clear the repository before each test, then verify getPersonById, ensure it returns null when not found, test fetching by multiple nationalities, and handle both null and empty‐list inputs.  
</rationale>
<newLines>
package pl.piomin.services.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.model.Person;
import pl.piomin.services.model.Gender;
import pl.piomin.services.repository.PersonRepository;
import pl.piomin.services.tools.PersonTools;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PersonToolsIntegrationTest {

    @Autowired
    private PersonTools personTools;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
    }

    @Test
    void shouldFindPersonById() {
        Person person = new Person();
        person.setName("Alice");
        person.setNationality("Polish");
        person.setAge(30);
        person.setGender(Gender.FEMALE);
        personRepository.save(person);

        Person found = personTools.getPersonById(person.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(person.getId());
        assertThat(found.getName()).isEqualTo("Alice");
    }

    @Test
    void shouldReturnNullWhenNotFound() {
        Person found = personTools.getPersonById(999L);
        assertThat(found).isNull();
    }

    @Test
    void shouldFindByNationality() {
        Person p1 = new Person();
        p1.setName("Bob");
        p1.setNationality("German");
        Person p2 = new Person();
        p2.setName("Charlie");
        p2.setNationality("Polish");
        Person p3 = new Person();
        p3.setName("Dana");
        p3.setNationality("Spanish");
        personRepository.saveAll(List.of(p1, p2, p3));

        List<Person> result = personTools.getPersonsByNationality(List.of("Polish", "German"));
        assertThat(result)
            .hasSize(2)
            .extracting(Person::getNationality)
            .containsExactlyInAnyOrder("Polish", "German");
    }

    @Test
    void shouldHandleNullNationality() {
        Person person = new Person();
        person.setName("Eve");
        personRepository.save(person);

        assertThat(personTools.getPersonsByNationality(null)).isEmpty();
        assertThat(personTools.getPersonsByNationality(List.of())).isEmpty();
    }
}
</newLines>

<rationale>
Implemented repository integration tests in PersonRepositoryIntegrationTest.java.  We verify save/findById behavior, querying by nationality, updating fields, and ensure that all values of the Gender enum are properly persisted and retrieved.
</rationale>
<newLines>
package pl.piomin.services.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.model.Person;
import pl.piomin.services.model.Gender;
import pl.piomin.services.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PersonRepositoryIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        Person person = new Person();
        person.setName("Frank");
        person.setNationality("Italian");
        person.setAge(40);
        person.setGender(Gender.MALE);
        Person saved = personRepository.save(person);

        Optional<Person> found = personRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Frank");
    }

    @Test
    void shouldFindByNationality() {
        Person p1 = new Person();
        p1.setName("Gina");
        p1.setNationality("Polish");
        p1.setAge(25);
        p1.setGender(Gender.FEMALE);
        Person p2 = new Person();
        p2.setName("Hank");
        p2.setNationality("Polish");
        p2.setAge(30);
        p2.setGender(Gender.MALE);
        personRepository.saveAll(List.of(p1, p2));

        List<Person> result = personRepository.findByNationality("Polish");
        assertThat(result)
            .hasSize(2)
            .extracting(Person::getNationality)
            .containsOnly("Polish");
    }

    @Test
    void shouldUpdateFields() {
        Person person = new Person();
        person.setName("Ian");
        person.setNationality("Brazilian");
        person.setAge(50);
        person.setGender(Gender.MALE);
        Person saved = personRepository.save(person);

        saved.setName("Ian Updated");
        Person updated = personRepository.save(saved);
        Optional<Person> found = personRepository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ian Updated");
    }

    @Test
    void shouldSupportAllGenders() {
        for (Gender gender : Gender.values()) {
            Person p = new Person();
            p.setName("Test " + gender);
            p.setNationality("X");
            p.setAge(20);
            p.setGender(gender);
            personRepository.save(p);
        }

        List<Person> all = personRepository.findAll();
        assertThat(all)
            .hasSize(Gender.values().length)
            .extracting(Person::getGender)
            .containsExactlyInAnyOrder(Gender.values());
    }
}
</newLines>

<rationale>
Added bean‐configuration tests in ToolCallbackProviderIntegrationTest.java to ensure that the Spring bean ToolCallbackProvider and our PersonTools are present in the context, and that PersonTools is properly annotated with @Service.
</rationale>
<newLines>
package pl.piomin.services.config;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.execution.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.piomin.services.tools.PersonTools;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ToolCallbackProviderIntegrationTest {

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @Autowired
    private PersonTools personTools;

    @Test
    void beansExist() {
        assertThat(toolCallbackProvider).isNotNull();
        assertThat(personTools).isNotNull();
    }

    @Test
    void toolsIsService() {
        assertThat(PersonTools.class).hasAnnotation(Service.class);
    }
}
</newLines>

<rationale>
Created a test‐specific application configuration (application-test.yml) to disable the MCP server, configure an in-memory H2 datasource with create-drop DDL, and set DEBUG logging for our service and relevant frameworks.
</rationale>
<newLines>
spring:
  ai:
    mcp:
      server:
        enabled: false
  datasource:
    url: jdbc:h2:mem:testdb-person
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
server:
  port: 0
logging:
  level:
    pl.piomin.services: DEBUG
    org.springframework.ai: DEBUG
    org.hibernate.SQL: DEBUG
</newLines>

<rationale>
Added the necessary test dependencies to pom.xml so that Spring Boot testing, Testcontainers, H2 database support, and AssertJ assertions are available under the test scope.
</rationale>
<newLines>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-testcontainers</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>