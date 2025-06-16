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

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Unremovable
public class ServerResponseHandler {

  @Inject
  OutputUtils outputUtils;

  private static final Logger LOGGER = Logger.getLogger(ServerResponseHandler.class);

  public static final String DELETE_MESSAGE = "Resource deleted successfully";
  public static final String UPDATE_MESSAGE = "Resource updated successfully";
  public static final String CREATE_MESSAGE = "Resource created successfully";
  public static final String GET_MESSAGE = "Resource get successfully";
  public static final String EMPTY_MESSAGE = "Empty []";

  /**
   * Handle the server response
   *
   * @param response the server response
   * @param <T>      the response if returned by the server
   * @return null if server does not send a resource else return the resource
   */
  @SuppressWarnings("unchecked")
  public <T extends ResourceNode> T handleServerResponse(ServerResponse<T> response, String message) {
    if (response.getResource() instanceof User resource) {
      if (response.isSuccess()) {
        if (response.getResource().isEmpty())
          outputUtils.logMsg(LOGGER, Logger.Level.INFO, EMPTY_MESSAGE);
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, message);
        return (T) resource;
      } else
        handleError(response);

    } else if (response.getResource() instanceof Group resource) {
      if (response.isSuccess()) {
        if (response.getResource().isEmpty())
          outputUtils.logMsg(LOGGER, Logger.Level.INFO, EMPTY_MESSAGE);
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, message);
        return (T) resource;
      } else
        handleError(response);
    } else if (response.getResource() instanceof Schema) {
      if (response.getResource().isEmpty())
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, EMPTY_MESSAGE);
      if (response.isSuccess()) {
        var resource = (Group) response.getResource();
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, message);
        return (T) resource;
      } else
        handleError(response);
    }
    if (response.isSuccess()) {
      outputUtils.logMsg(LOGGER, Logger.Level.INFO, message);
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
    if (ServerResponse.getResource().isEmpty()) {
      outputUtils.logMsg(LOGGER, Logger.Level.INFO, "error response : " + EMPTY_MESSAGE);
    }
    if (ServerResponse.getErrorResponse() == null && ServerResponse.getResource() == null) {
      throw new RuntimeException("No response body, error not in RFC7644: " + ServerResponse.getResponseBody());
    } else {
      checkAlreadyCreatedResource(ServerResponse);
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
      if (bulkResponse.isEmpty()) {
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, EMPTY_MESSAGE);
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, "bulk response : " + bulkResponse);
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, "Failed Operations : " + bulkResponse.getFailedOperations());
        outputUtils.logMsg(LOGGER, Logger.Level.INFO,
            "Successful Operations : " + bulkResponse.getSuccessfulOperations());
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, "HTTP Status : " + bulkResponse.getHttpStatus());
      } else if (response.getErrorResponse() == null && response.getResource() == null) {
        throw new RuntimeException("No response body, error not in RFC7644: " + response.getResponseBody());
      } else if (response.getErrorResponse() == null) {
        checkAlreadyCreatedResource(response.getResource());
        // throw new RuntimeException("Bulk error: " + response.getResponseBody());
      } else {
        throw new BadRequestException("Bad request: " + response.getErrorResponse());
      }
    }
  }

  public <T extends ResourceNode> List<T> handleListResources(ServerResponse<ListResponse<T>> response)
      throws BadRequestException {
    if (response.isSuccess()) {
      if (response.getResource().isEmpty())
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, EMPTY_MESSAGE);
      outputUtils.logMsg(LOGGER, Logger.Level.INFO, GET_MESSAGE);
      return response.getResource().getListedResources();
    }
    if (response.getErrorResponse() == null) {
      checkAlreadyCreatedResource(response.getResource());
      throw new BadRequestException(
          "Invalid response format: " + "\nbody :" + response.getResponseBody() + ",\nerror : "
              + response.getErrorResponse() + ",\nstatus: " + response.getHttpStatus() + ",\nheader : "
              + response.getHttpHeaders() + ",\ninfo : ");
    }
    throw new BadRequestException(response.getResponseBody());
  }

  private <T extends ResourceNode> void checkAlreadyCreatedResource(ListResponse<T> serverResponse)
      throws BadRequestException {
    var idList = new ArrayList<String>();
    for (var resp : serverResponse.getListedResources()) {
      idList.add(resp.get("id").asText());
    }
    throw new BadRequestException(
        "liste resource error : cannot create resources, already created : " + idList.toString());
  }

  private <T extends ResourceNode> void checkAlreadyCreatedResource(BulkResponse serverResponse)
      throws BadRequestException {
    var idList = new ArrayList<String>();

    var operations = serverResponse.get("Operations");
    if (operations.isArray() && !operations.isNull()) {
      for (var item : operations) {
        idList.add(item.get("response").toPrettyString());
      }
    }
    throw new BadRequestException("bulk error : cannot create resources, already created : " + idList.toString());
  }

  private <T extends ResourceNode> void checkAlreadyCreatedResource(ServerResponse<?> serverResponse) {
    if (serverResponse.getHttpStatus() == 409) {
      throw new BadRequestException("response error : cannot create resource, already created : "
          + serverResponse.getResource().get("id").asText());
    }
  }
}