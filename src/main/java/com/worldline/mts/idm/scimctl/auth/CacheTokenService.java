package com.worldline.mts.idm.scimctl.auth;

import java.nio.file.Files;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CacheTokenService {

  @Inject
  ScimCtlBeansConfig config;

  private Path path;
  private static final String CACHE_NAME = ".scim_ctl_token_cache";
  private static final String CACHE_EXTENSION = ".json";

  private ObjectMapper mapper;

  /**
   * To prevent 401 response form server if expirationDate in the current
   * access_token is to close form now.
   */
  private static final int SAFETY_MARGIN_MS = 3000;

  /**
   * replace bash script
   */
  @PostConstruct
  private void initCache() {
    this.mapper = config.getObjectMapper();
    try {
      this.path = Files.createTempFile(CACHE_NAME, CACHE_EXTENSION);
    } catch (IOException e) {
      System.err.println("Error while creating token cache at " + this.path.toString());
    }

  }

  /**
   * @return a json node composed by 2 keys acces_token and expiredAt
   * @return null if empty
   */
  public JsonNode readTokenFromChache() {
    JsonNode token = null;
    try {
      token = mapper.readTree(new File(this.path.toUri()));
    } catch (IOException e) {
      System.err.println("Error while reading token form cache at " + this.path);
    }
    return token;
  }


  public void writeTokenToCache(String token, Long expiredAt) {
    try {
      var objectNode = mapper.createObjectNode();
      objectNode.put("access_token", token);
      objectNode.put("expireAt", expiredAt);
      mapper.writeValue(new File(this.path.toUri()), objectNode);
    } catch (IOException e) {
      System.err.println("Error while reading token form cache at " + this.path + e.getMessage());
    }
  }

  /*
   * *100l -> expiration date token en s, timesMili en ms
   * -3000 pr eviter des 401
   */
  public boolean isExpired(JsonNode token) {
    var expirationTime = token.get("expireAt").asLong();
    var expirationDate = Date.from(Instant.ofEpochMilli(expirationTime * 1000L - SAFETY_MARGIN_MS));
    var now = new Date();
    return (now.after(expirationDate));
  }
}
