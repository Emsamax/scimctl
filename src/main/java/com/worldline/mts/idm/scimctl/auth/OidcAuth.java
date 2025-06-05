package com.worldline.mts.idm.scimctl.auth;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.runtime.TokensHelper;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/cli")
public class OidcAuth {

  @Inject
  OidcServiceAccountCreator serviceAccount;
  TokensHelper tokenHelper = new TokensHelper();

  @Inject
  @RestClient
  RestClientWithTokenHeaderParam restClientWithTokenHeaderParam;

  @Inject
  OidcClient client;

  @Inject

  @GET
  @Path("user-name")
  @Produces("text/plain")
  public Uni<String> getUserName() {
    return tokenHelper.getTokens(client).onItem()
                      .transformToUni(tokens -> restClientWithTokenHeaderParam.getUserName("Bearer " + tokens.getAccessToken()));
  }

  @GET
  @Path("get-access-with-bearer-token")
  @Produces("text/plain")
  public Uni<String> getAccessWithBearerToken() {
    return tokenHelper.getTokens(serviceAccount.getOidcClient()).onItem()
                      .transformToUni(tokens -> restClientWithTokenHeaderParam.getAdminName("Bearer " + tokens.getAccessToken()));
  }
}
