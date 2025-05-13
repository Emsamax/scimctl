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
    public SchemaService() {
    }

    @Inject
    ClientConfig config;

    @CommandLine.ArgGroup(multiplicity = "1")
    CommonOptions commonOptions;

    public Schema getSchema() throws BadRequestException {
        ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig());
        String endpointPath = EndpointPaths.SCHEMAS;
        ServerResponse<Schema> response = scimRequestBuilder.get(Schema.class, endpointPath, config.getSCHEMA_ID()).sendRequest();
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

