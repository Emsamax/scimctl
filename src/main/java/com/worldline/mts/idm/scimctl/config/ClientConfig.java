package com.worldline.mts.idm.scimctl.config;

import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

import com.worldline.mts.idm.scimctl.auth.TokenService;

/**
 * used to instantiate a ScimClientConfig object
 */


@ApplicationScoped
public class ClientConfig {

  @Inject
  TokenService tokenService;

  public ScimClientConfig getScimClientConfig() {
    return ScimClientConfig.builder()
        .connectTimeout(5)
        .requestTimeout(5)
        .socketTimeout(5)
        .httpHeaders(Map.of("Authorization", "Bearer " + tokenService.getToken()))
        .hostnameVerifier((s, sslSession) -> true)
        .build();
  }
}
