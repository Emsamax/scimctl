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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

  private String token = "";

  private Long expirationDate;

  private volatile Tokens currentTokens;

  private static final String CACHE = "/tmp/.scim_ctl_token_cache.txt";

  @PostConstruct
  private void init() {
    getInitialAccessToken();
    writeTokenToCache(currentTokens.getAccessToken(), this.expirationDate);
    fetchTokens();

  }

  /**
   * Fetch token from cache
   * If token in cache is expired, get a refresh token and write in cache
   * 
   * @return valid Access/Refresh token
   */
  public String getToken() {
    fetchTokens();
    return this.token;
  }

  private void getInitialAccessToken() {
    try {
      utils.logMsg("get access token");
      currentTokens = oidcClient.getTokens().await().indefinitely();
      expirationDate = currentTokens.getAccessTokenExpiresAt();
    } catch (CompletionException e) {
      LOGGER.info(e.getMessage());
    } catch (OidcClientException e ){
      System.err.println(e.getMessage() + " at " + this.authServerUrl);
      System.exit(-1);
    }
  }

  /**
   * retrieve access/refresh token from cache, check if expired if yes get refresh
   * token
   */
  private void fetchTokens() {
    readTokenFromChache();
    if (isExpired(this.token)) {
      utils.logMsg("get new access token");
      getInitialAccessToken();
    }
  }

  private void readTokenFromChache() {

    try (var fileReader = new FileReader(CACHE)) {
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      this.token = bufferedReader.readLine();
      this.expirationDate = Long.valueOf(bufferedReader.readLine());
      bufferedReader.close();
    } catch (IOException e) {
      System.err.println("Error while reading token form cache at " + CACHE);
    }
  }

  private void writeTokenToCache(String token, long expirationDate) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE));
      writer.write(token);
      writer.newLine();
      writer.write(String.valueOf(expirationDate));
      writer.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /*
   * *100l -> expiration date token en s, timesMili en ms
   * -3000 pr eviter des 401
   */
  private boolean isExpired(String token) {
    return (System.currentTimeMillis() >= expirationDate * 1000L - 3000);
  }

}