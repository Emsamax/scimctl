package com.worldline.mts.idm.scimctl.config.auth;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static io.restassured.RestAssured.given;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.worldline.mts.idm.scimctl.auth.CacheTokenService;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;

@QuarkusTest
public class CacheTest {

  private static final String CACHE = ".scim_ctl_token_cache.txt";
  private static final String KEYCLOAK_URL = "http://localhost:8081/realms/scim/";

  private RequestSpecification authnSpec;
  private String accessToken;

  @BeforeEach
  public void setUp() {
    var accessToken = FakeToken.generate(UUID.randomUUID().toString(), "admin", "view-resource");
    this.accessToken = accessToken;
    authnSpec = given().header("Authorization", "Bearer " + accessToken);
  }

  @Inject
  CacheTokenService cache;

  @Order(1)
  @Test
  public void constructorTest() {
    assertNotNull(cache);
  }

  @Order(2)
  @Test
  public void cacheTest() {
    var currentAccessToken = this.accessToken;
    var expiredAt = Instant.now().toEpochMilli();
    cache.writeTokenToCache(accessToken, expiredAt);
    var readToken = cache.readTokenFromChache();
    assertEquals(currentAccessToken, readToken.get("access_token").asText());
    assertEquals(expiredAt, readToken.get("expireAt").asLong());
  }

  @Order(3)
  @Test
  public void tokenRenewalTest() {
    var expiredAt = Instant.now().getEpochSecond();
    System.out.println("ExpireAt : " + expiredAt);
    cache.writeTokenToCache(accessToken, expiredAt);
    var readToken = cache.readTokenFromChache();
    System.out.println(readToken.get("expireAt"));
    assertTrue(cache.isExpired(readToken));
  }

}
