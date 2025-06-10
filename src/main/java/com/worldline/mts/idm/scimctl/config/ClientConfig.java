package com.worldline.mts.idm.scimctl.config;

import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.keys.KeyStoreWrapper;
import io.quarkus.oidc.client.Tokens;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import java.util.Map;

/**
 *  used to instantiate a ScimClientConfig object
 */


@ApplicationScoped
public class ClientConfig {

  @Inject
  Tokens tokens;

  private String accessToken;


  @GET
  public String getAccessToken() {
    this.accessToken = tokens.getAccessToken();
    System.out.println(this.accessToken);
    return this.accessToken;
  }

  private final ScimRequestBuilder scimRequestBuilder;

  public ClientConfig() {
    this.scimRequestBuilder = new ScimRequestBuilder("http://localhost:8080/base/scim/v2", this.getScimClientConfig());
  }

  private KeyStoreWrapper wrapper;

  public String getSchemaId() {
    return "urn:ietf:params:scim:schemas:core:2.0:Group";
  }

  public ScimRequestBuilder getScimRequestBuilder() {
    return this.scimRequestBuilder;
  }

  public ScimClientConfig getScimClientConfig() {
    return ScimClientConfig.builder()
                           .connectTimeout(5)
                           .requestTimeout(5)
                           .socketTimeout(5)
                           .clientAuth(wrapper)
                           .httpHeaders(Map.of("Authorization", "Bearer " + getAccessToken()))
                           .hostnameVerifier((s, sslSession) -> true)
                           .build();
  }
}
