package com.worldline.mts.idm.scimctl.config.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.google.inject.Inject;
import com.worldline.mts.idm.scimctl.auth.TokenService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TokenServiceIT {

  @Inject
  TokenService tokenService;

  @Test
  @Order(0)
  public void constructorTest() {
    assertNotNull(tokenService);
    assertNotNull(tokenService.getCurrentToken());
  }

  @Test
  @Order(1)
  public void testRefreshToken() {
      tokenService.getCurrentToken().getAccessToken();
  }
}
