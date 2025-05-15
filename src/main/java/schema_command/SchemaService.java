package schema_command;

import cli.ClientConfig;
import common.CommonOptions;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.schemas.Schema;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import picocli.CommandLine;


@ApplicationScoped
@Named("schemaService")
@Unremovable
public class SchemaService {

    @Inject
    ClientConfig config;

    public Schema getSchema() throws BadRequestException {
        ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), config.getScimClientConfig());
        String endpointPath = EndpointPaths.SCHEMAS;
        ServerResponse<Schema> response = scimRequestBuilder.get(Schema.class, endpointPath, config.getSchemaId()).sendRequest();
        if (response.isSuccess()) {
            return response.getResource();
        } else if (response.getErrorResponse() == null) {
            // the response was not an error response as described in RFC7644
            throw new BadRequestException("error isn't describe in RFC7644 :" + response.getResponseBody());
        } else {
            throw new BadRequestException(response.getResponseBody());
        }
    }
}

