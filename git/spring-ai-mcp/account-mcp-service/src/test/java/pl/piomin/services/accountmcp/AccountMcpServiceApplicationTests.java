package pl.piomin.services.accountmcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AccountMcpServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context (including MCP tools) loads without errors
    }
}
</newLines>

<rationale>
Created AccountToolsIntegrationTest.java to integrate-test the AccountTools bean against the AccountRepository, covering normal, empty and null input scenarios using AssertJ.
</rationale>
<newLines>
package pl.piomin.services.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.model.Account;
import pl.piomin.services.repository.AccountRepository;
import pl.piomin.services.tools.AccountTools;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountToolsIntegrationTest {

    @Autowired
    private AccountTools accountTools;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void shouldReturnAccountsForPerson() {
        Account a1 = new Account();
        a1.setId("1");
        a1.setPersonId("p1");
        a1.setBalance(100);
        accountRepository.save(a1);
        Account a2 = new Account();
        a2.setId("2");
        a2.setPersonId("p1");
        a2.setBalance(200);
        accountRepository.save(a2);

        List<Account> results = accountTools.getAccountsByPersonId("p1");
        assertThat(results)
            .hasSize(2)
            .extracting(Account::getId)
            .containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void shouldReturnEmptyWhenNone() {
        List<Account> results = accountTools.getAccountsByPersonId("ghost");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldHandleNullInput() {
        List<Account> results = accountTools.getAccountsByPersonId(null);
        assertThat(results).isNotNull().isEmpty();
    }
}
</newLines>

<rationale>
Created AccountRepositoryIntegrationTest.java to verify that AccountRepository correctly saves and retrieves Account entities by ID and personId, including handling empty results and negative balances.
</rationale>
<newLines>
package pl.piomin.services.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.model.Account;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        Account account = new Account();
        account.setId("10");
        account.setPersonId("p10");
        account.setBalance(50);
        accountRepository.save(account);

        Optional<Account> found = accountRepository.findById("10");
        assertThat(found).isPresent()
                         .get()
                         .isEqualTo(account);
    }

    @Test
    void shouldFindByPersonId() {
        Account a1 = new Account();
        a1.setId("11");
        a1.setPersonId("p11");
        a1.setBalance(150);
        accountRepository.save(a1);
        Account a2 = new Account();
        a2.setId("12");
        a2.setPersonId("p11");
        a2.setBalance(250);
        accountRepository.save(a2);

        List<Account> results = accountRepository.findByPersonId("p11");
        assertThat(results)
            .hasSize(2)
            .extracting(Account::getId)
            .containsExactlyInAnyOrder("11", "12");
    }

    @Test
    void shouldReturnEmptyForUnknownPerson() {
        List<Account> results = accountRepository.findByPersonId("unknown");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldHandleNegativeBalances() {
        Account negative = new Account();
        negative.setId("neg1");
        negative.setPersonId("pn");
        negative.setBalance(-100);
        accountRepository.save(negative);

        List<Account> results = accountRepository.findByPersonId("pn");
        assertThat(results)
            .hasSize(1)
            .first()
            .matches(a -> a.getBalance() < 0);
    }
}
</newLines>

<rationale>
Created ToolCallbackProviderIntegrationTest.java to verify that both the ToolCallbackProvider and AccountTools beans are present in the context and that AccountTools is annotated as a Spring service.
</rationale>
<newLines>
package pl.piomin.services.config;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.execution.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.piomin.services.tools.AccountTools;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ToolCallbackProviderIntegrationTest {

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @Autowired
    private AccountTools accountTools;

    @Test
    void beansExist() {
        assertThat(toolCallbackProvider).isNotNull();
        assertThat(accountTools).isNotNull();
    }

    @Test
    void toolsIsService() {
        assertThat(AccountTools.class.isAnnotationPresent(org.springframework.stereotype.Service.class)).isTrue();
    }
}
</newLines>

<rationale>
Added application-test.yml to configure an in-memory H2 database, disable MCP server, and set logging levels for the test profile.
</rationale>
<newLines>
spring:
  ai:
    mcp:
      server:
        enabled: false
  datasource:
    url: jdbc:h2:mem:testdb-account
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
Added test dependencies to the POM to support Spring Boot testing, Testcontainers, H2, and AssertJ in the test scope.
</rationale>
<newLines>
<!-- Test dependencies -->
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