package com.worldline.mts.idm.scimctl.auth;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.runtime.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@ApplicationScoped
public class OidcServiceAccountCreator {

  @Inject
  OidcClients oidcClients;

  private volatile OidcClient oidcClient;

  //TODO : env var for oidc variables
 // @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String keycloakClient ="//localhost:8081/realms/scim";
 // @ConfigProperty(name = "quarkus.oidc.client-id")
  String ClientId ="scim-ctl";
 // @ConfigProperty(name = "quarkus.oidc.credentials.secret")
  String ClientSecret = "8naJ78ay1to1xGjEnyhc9VxPpTgxyBqh";

  public void startup(@Observes StartupEvent event) {
    createOidcClient().subscribe().with(client -> {
      oidcClient = client;
    });
  }

  public OidcClient getOidcClient() {
    return oidcClient;
  }

  private Uni<OidcClient> createOidcClient() {
    OidcClientConfig cfg = OidcClientConfig
      .authServerUrl(keycloakClient)
      .id("keycloak")
      .clientId(ClientId)
      .credentials(ClientSecret)
      .grant(OidcClientConfig.Grant.Type.CLIENT)
      .build();
    return oidcClients.newClient(cfg);
  }
}
