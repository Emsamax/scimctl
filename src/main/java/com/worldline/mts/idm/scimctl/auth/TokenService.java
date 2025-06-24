package com.worldline.mts.idm.scimctl.auth;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientException;
import io.quarkus.oidc.client.Tokens;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import static org.jboss.logging.Logger.getLogger;

import java.util.concurrent.CompletionException;

@ApplicationScoped
public class TokenService {
  private static final Logger LOGGER = getLogger(TokenService.class);

  private String authServerUrl;

  public TokenService(@ConfigProperty(name = "quarkus.oidc-client.auth-server-url") String authServerUrl) {
    this.authServerUrl = authServerUrl;
  }

  @Inject
  OutputUtils utils;

  @Inject
  OidcClient oidcClient;

  @Inject
  CacheTokenService cache;


  private volatile Tokens currentTokens;

  @PostConstruct
  private void init() {
    getInitialAccessToken();
  }

  /**
   * Fetch token from cache
   * If token in cache is expired, get a refresh token and write in cache
   * 
   * @return valid Access/Refresh token
   */
  public String getToken() {
    return fetchTokens();
  }

  private void getInitialAccessToken() {
    try {
      utils.logMsg("get access token");
      currentTokens = oidcClient.getTokens().await().indefinitely();
      cache.writeTokenToCache(currentTokens.getAccessToken(), currentTokens.getAccessTokenExpiresAt());
    } catch (CompletionException e) {
      LOGGER.info(e.getMessage());
    } catch (OidcClientException e) {
      System.err.println(e.getMessage() + " at " + this.authServerUrl);
      System.exit(-1);
    }
  }

  /**
   * retrieve access/refresh token from cache, check if expired if yes get refresh
   * token
   */
  private String fetchTokens() {
    var currentAccessToken = cache.readTokenFromChache();
    if (cache.isExpired(currentAccessToken)) {
      utils.logMsg("get new access token");
      getInitialAccessToken();
      currentAccessToken = cache.readTokenFromChache();
    }
    return currentAccessToken.get("access_token").asText();
  }
}