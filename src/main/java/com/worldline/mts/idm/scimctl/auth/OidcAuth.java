package com.worldline.mts.idm.scimctl.auth;

import io.quarkus.oidc.client.Tokens;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class OidcAuth {

  @Inject
  Tokens tokens;

  @GET
  public String getAccessToken(){
    String accessToken = tokens.getAccessToken();
    System.out.println(accessToken);
    return accessToken;
  }

}
