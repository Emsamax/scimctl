package com.worldline.mts.idm.scimctl.utils;

import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import de.captaingoldfish.scim.sdk.common.response.ListResponse;
import de.captaingoldfish.scim.sdk.common.schemas.Schema;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
@Unremovable
public class ServerResponseHandler {

  @Inject
  Logger LOGGER;

  public static final String DELETE_MESSAGE = "Resource deleted successfully";
  public static final String UPDATE_MESSAGE = "Resource updated successfully";
  public static final String CREATE_MESSAGE = "Resource created successfully";
  public static final String GET_MESSAGE = "Resource get successfully";

  /**
   * Handle the server response
   *
   * @param response the server response
   * @param <T>      the response if returned by the server
   * @return null if server does not send a resource else return the resource
   */
  public <T extends ResourceNode> T handleServerResponse(ServerResponse<?> response, String message) {
    if (response.getResource() instanceof User resource) {
      if (response.isSuccess()) {
        LOGGER.log(Logger.Level.valueOf("INFO"), message + " : " + resource.toPrettyString());
        return (T) resource;
      } else
        handleError(response);

    } else if (response.getResource() instanceof Group resource) {
      if (response.isSuccess()) {
        LOGGER.log(Logger.Level.valueOf("INFO"), message + " : " + resource.toPrettyString());
        return (T) resource;
      } else
        handleError(response);
    } else if (response.getResource() instanceof Schema) {
      if (response.isSuccess()) {
        var resource = (Group) response.getResource();
        LOGGER.log(Logger.Level.valueOf("INFO"), message + " : " + resource.toPrettyString());
        return (T) resource;
      } else
        handleError(response);
    }
    if (response.isSuccess()) {
      LOGGER.log(Logger.Level.valueOf("INFO"), message);
    } else
      handleError(response);

    return null;
  }

  /**
   * If response is not succes
   *
   * @param ServerResponse
   */
  private void handleError(ServerResponse<?> ServerResponse) throws RuntimeException, BadRequestException {
    if (ServerResponse.getErrorResponse() == null && ServerResponse.getResource() == null) {
      throw new RuntimeException("No response body, error not in RFC7644: " + ServerResponse.getResponseBody());
    } else {
      throw new BadRequestException("Bad request: " + ServerResponse.getErrorResponse());
    }
  }

  /**
   * If bulk different handle
   *
   * @param response
   */
  public void handleBulkResponse(ServerResponse<BulkResponse> response) {
    if (response.isSuccess()) {
      BulkResponse bulkResponse = response.getResource();
      LOGGER.log(Logger.Level.valueOf("INFO"), "Bulk Response : " + bulkResponse);
      LOGGER.log(Logger.Level.valueOf("INFO"), "Failed Operations :" + bulkResponse.getFailedOperations());
      LOGGER.log(Logger.Level.valueOf("INFO"), "Successful Operations : " + bulkResponse.getSuccessfulOperations());
      LOGGER.log(Logger.Level.valueOf("INFO"), "HTTP Status : " + bulkResponse.getHttpStatus());
    } else if (response.getErrorResponse() == null && response.getResource() == null) {
      throw new RuntimeException("No response body, error not in RFC7644: " + response.getResponseBody());
    } else if (response.getErrorResponse() == null) {
      throw new RuntimeException("Bulk error: " + response.getResponseBody());
    } else {
      throw new BadRequestException("Bad request: " + response.getErrorResponse());
    }
  }

  public <T extends ResourceNode> List<T> handleListResources(ServerResponse<ListResponse<T>> response) {
    if (response.isSuccess()) {
      if (response.getResource().isEmpty())
        System.out.println("EMPTY");
      return response.getResource().getListedResources();
    }
    if (response.getErrorResponse() == null) {
      throw new BadRequestException(
          "Invalid response format: " + "\nbody :" + response.getResponseBody() + ",\nerror : "
              + response.getErrorResponse() + ",\nstatus: " + response.getHttpStatus() + ",\nheader : "
              + response.getHttpHeaders() + ",\ninfo : ");
    }
    throw new BadRequestException(response.getResponseBody());
  }
}
