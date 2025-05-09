package cli;

import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import de.captaingoldfish.scim.sdk.server.endpoints.authorize.Authorization;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.Map;
import java.util.Set;


@ApplicationScoped
@Named("clientConfig")
@Unremovable

/**
 *  used to instantiate a ScimClientConfig object
 */
public class ClientConfig implements Authorization {

    public ClientConfig() {
    }

    public ScimClientConfig getScimClientConfig() {
        return ScimClientConfig.builder()
                .connectTimeout(5)
                .requestTimeout(5)
                .socketTimeout(5)
                .hostnameVerifier((s, sslSession) -> true)
                .build();
    }

    @Override
    public Set<String> getClientRoles() {
        return Set.of();
    }

    @Override
    public boolean authenticate(Map<String, String> map, Map<String, String> map1) {
        return true;
    }


    /*public ScimService getScimService() {
        Client client = ClientBuilder.newClient().register(OAuth2ClientSupport.feature("..bearerToken.."));
        WebTarget target = client.target("http://localhost:8080/base/scim/v2");
        return new ScimService(target);
    }*/
}
