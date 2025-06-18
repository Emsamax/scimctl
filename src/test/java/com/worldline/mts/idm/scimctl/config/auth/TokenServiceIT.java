package com.worldline.mts.idm.scimctl.config.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.worldline.mts.idm.scimctl.auth.TokenService;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class TokenServiceIT {

  @Inject
  TokenService tokenService;

  private String currentAccessToken;

  @Test
  @Order(0)
  public void constructorTest() {
    assertNotNull(tokenService);
  }


  //TODO : refactor tests to inclide paths and file verification
  /* 
  @Test
  @Order(1)
  public void testFetchAccessToken() {
    assertNotNull(tokenService.getToken());
    this.currentAccessToken = tokenService.getCurrentToken().getAccessToken().toString();
    tokenService.fetchTokens();
    assertNotNull(currentAccessToken, tokenService.getCurrentToken().getAccessToken().toString());
  }
    */

}
