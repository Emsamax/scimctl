package com.worldline.mts.idm.scimctl.auth;

import io.netty.handler.logging.LogLevel;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;

import org.jboss.logging.Logger;

import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import static org.jboss.logging.Logger.getLogger;

import java.util.concurrent.CompletionException;

@ApplicationScoped
public class TokenService {
  private static final Logger LOGGER = getLogger(TokenService.class);

  @Inject
  OutputUtils utils;

  @Inject
  OidcClient oidcClient;

  private volatile Tokens currentTokens;

  public Tokens getCurrentToken() {
    return this.currentTokens;
  }

  public void getAccessToken() {
    try {
      utils.logMsg(LOGGER, Logger.Level.INFO, "get initial access token");
      currentTokens = oidcClient.getTokens().await().indefinitely();
    } catch (CompletionException e) {
      LOGGER.info(e.getMessage());
    }
  }

  @GET
  public void fetchTokens() {
    currentTokens = oidcClient.getTokens().await().indefinitely();
    if (currentTokens.isAccessTokenExpired()) {
       utils.logMsg(LOGGER, Logger.Level.INFO, "get refresh token");
      currentTokens = oidcClient.refreshTokens(currentTokens.getRefreshToken()).await().indefinitely();
    } else {
      getAccessToken();
    }
  }
}