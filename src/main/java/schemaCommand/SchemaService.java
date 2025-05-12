package schemaCommand;

import cli.ClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.schemas.Schema;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;


@ApplicationScoped
@Named("schemaService")
@Unremovable
public class SchemaService {


    @Inject
    public SchemaService() {
    }

    @Inject
    ClientConfig config;

    public Schema getSchema() {
        try (ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig())) {
            String endpointPath = EndpointPaths.SCHEMAS;
            ServerResponse<Schema> response = scimRequestBuilder.get(Schema.class, endpointPath, config.getSCHEMA_ID()).sendRequest();
            return response.getResource();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
