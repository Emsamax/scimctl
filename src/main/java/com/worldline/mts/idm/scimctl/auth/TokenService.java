package com.worldline.mts.idm.scimctl.auth;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;

import org.jboss.logging.Logger;
import static org.jboss.logging.Logger.getLogger;

import java.util.concurrent.CompletionException;

@ApplicationScoped
public class TokenService {
  private static final Logger LOGGER = getLogger(TokenService.class);

  @Inject
  OidcClient oidcClient;

  private volatile Tokens currentTokens;

  public Tokens getCurrentToken() {
    return this.currentTokens;
  }

  @PostConstruct
  public void init() {
    try {
      currentTokens = oidcClient.getTokens().await().indefinitely();
    } catch (CompletionException e) {
      LOGGER.info(e.getMessage());
    }
  }

  @GET
  public void fetchTokens() {
    currentTokens = oidcClient.getTokens().await().indefinitely();
    if (currentTokens.isAccessTokenExpired()) {
      currentTokens = oidcClient.refreshTokens(currentTokens.getRefreshToken()).await().indefinitely();
    } else {
      currentTokens = oidcClient.getTokens().await().indefinitely();
    }
  }
}