package cli;

import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import de.captaingoldfish.scim.sdk.server.endpoints.authorize.Authorization;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;
import java.util.Set;


@ApplicationScoped
@Named("clientConfig")
@Unremovable

/**
 *  used to instantiate a ScimClientConfig object
 */
public class ClientConfig implements Authorization {

    @ConfigProperty(name = "scim.base.url")
    String baseUrl;

    @ConfigProperty(name = "scim.schema.id")
    String schemaId;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSchemaId() {
        return schemaId;
    }

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

}
