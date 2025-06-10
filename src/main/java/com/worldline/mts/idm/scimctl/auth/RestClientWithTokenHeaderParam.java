package com.worldline.mts.idm.scimctl.auth;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@OidcClientFilter
@RegisterRestClient
@Path("/")
public interface RestClientWithTokenHeaderParam {
  @GET
  @Produces("text/plain")
  Uni<String> getToken(@HeaderParam("Authorization") String authorization);
}
