package com.worldline.mts.idm.scimctl.commands.schema_cmd;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.worldline.mts.idm.scimctl.config.ServerResponseHandler;
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

  public Schema getSchema() throws BadRequestException {
    String endpointPath = EndpointPaths.SCHEMAS;
    ServerResponse<Schema> response = config.getScimRequestBuilder().get(Schema.class, endpointPath, config.getSchemaId()).sendRequest();
    return responseHandler.handleServerResponse(response, ServerResponseHandler.GET_MESSAGE);
  }
}

