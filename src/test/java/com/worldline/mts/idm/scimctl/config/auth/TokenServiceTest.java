package com.worldline.mts.idm.scimctl.config.auth;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.worldline.mts.idm.scimctl.auth.TokenService;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;

@QuarkusTest
public class TokenServiceTest {

  private static final String CACHE = ".scim_ctl_token_cache.txt";
  private static final String KEYCLOAK_URL = "http://localhost:8081/realms/scim/";

  private RequestSpecification authnSpec;

  @BeforeEach
  public void setUp() {
    var accessToken = FakeToken.generate(UUID.randomUUID().toString(), "admin", "view-resource");
    authnSpec = given().header("Authorization", "Bearer " + accessToken);
  }

  @Inject
  TokenService tokenService;

  @Order(1)
  @Test
  public void constructorTest() {
    assertNotNull(tokenService);
  }

  @Order(2)
  @Test
  public void cacheTest(){

  }

  @Order(3)
  @Test
  public void tokenRenewalTest() {

  }

}
