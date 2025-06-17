package com.worldline.mts.idm.scimctl.commands.schema_cmd;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.worldline.mts.idm.scimctl.utils.ServerResponseHandler;

import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.schemas.Schema;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
@Named("schemaService")
@Unremovable
public class SchemaService {

  @Inject
  ClientConfig config;

  @Inject
  ServerResponseHandler responseHandler;

  ScimRequestBuilder requestBuilder = new ScimRequestBuilder("http://localhost:8080/base/scim/v2", config.getScimClientConfig());

  public Schema getSchema() throws BadRequestException {
    String endpointPath = EndpointPaths.SCHEMAS;
    ServerResponse<Schema> response = requestBuilder.get(Schema.class, endpointPath, config.getSchemaId())
        .sendRequest();
    return responseHandler.handleServerResponse(response, ServerResponseHandler.GET_MESSAGE).get();
  }
}
