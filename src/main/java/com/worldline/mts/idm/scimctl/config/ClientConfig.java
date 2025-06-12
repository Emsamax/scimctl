package com.worldline.mts.idm.scimctl.config;

import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.worldline.mts.idm.scimctl.auth.TokenService;

/**
 * used to instantiate a ScimClientConfig object
 */
@RegisterRestClient
@ApplicationScoped
public class ClientConfig {

  @Inject
  TokenService tokenService;

  public String getSchemaId() {
    return "urn:ietf:params:scim:schemas:core:2.0:Group";
  }

  public ScimClientConfig getScimClientConfig() {
    tokenService.fetchTokens();
    return ScimClientConfig.builder()
        .connectTimeout(5)
        .requestTimeout(5)
        .socketTimeout(5)
        .httpHeaders(Map.of("Authorization", "Bearer " + tokenService.getCurrentToken().getAccessToken()))
        .hostnameVerifier((s, sslSession) -> true)
        .build();
  }
}
