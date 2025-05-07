package schemaCommand;

import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.schemas.Schema;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.security.KeyStore;

@ApplicationScoped
@Named("schemaService")
@Unremovable
public class SchemaService {

    private static final String BASE_URL = "http://localhost:8080/base/scim/v2";

    @Inject
    public SchemaService() {
    }

    public Schema getSchema() {
        ScimClientConfig scimClientConfig = ScimClientConfig.builder()
                .connectTimeout(5)
                .requestTimeout(5)
                .socketTimeout(5)
                //.clientAuth(getClientAuthKeystore())
                //.truststore(getTruststore())
                .clientAuth(null)
                .truststore(null)
                .hostnameVerifier((s, sslSession) -> true)
                .build();

        try (ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(BASE_URL, scimClientConfig)) {
            String endpointPath = EndpointPaths.SCHEMAS;
            ServerResponse<Schema> response = scimRequestBuilder.get(Schema.class, endpointPath, "urn:ietf:params:scim:schemas:core:2.0:Group").sendRequest();
            return response.getResource();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
