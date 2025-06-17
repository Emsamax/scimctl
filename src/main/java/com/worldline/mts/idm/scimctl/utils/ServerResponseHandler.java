package com.worldline.mts.idm.scimctl.utils;

import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import de.captaingoldfish.scim.sdk.common.response.ListResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  public <T extends ResourceNode> Optional<T> handleServerResponse(ServerResponse<T> response, String message) {
    if (response.isSuccess()) {
      System.out.printf("%s ", message);
      return Optional.of(response.getResource());
    } else {
      handleError(response);
      return Optional.empty();
    }
  }

  /**
   * If response is not succes
   *
   * @param ServerResponse
   */
  private void handleError(ServerResponse<?> serverResponse) {
    checkAlreadyCreatedResource(serverResponse);
    if (serverResponse.getResource() == null && serverResponse.getErrorResponse() == null) {
      System.out.println(EMPTY_MESSAGE);
    }
    if (serverResponse.getErrorResponse() == null && serverResponse.getResource() == null) {
      System.err.println(
          "No response body, error not in RFC7644:" + serverResponse.getErrorResponse().get("detail").asText());
    } else {
      checkAlreadyCreatedResource(serverResponse);
      System.err.println(serverResponse.getErrorResponse().get("detail").asText());
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
        System.out.println(EMPTY_MESSAGE);
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, "bulk response : " + bulkResponse);
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, "Failed Operations : " + bulkResponse.getFailedOperations());
        System.out.println("Successful Operations : " + bulkResponse.getSuccessfulOperations());
        outputUtils.logMsg(LOGGER, Logger.Level.INFO, "HTTP Status : " + bulkResponse.getHttpStatus());
      } else if (response.getErrorResponse() == null && response.getResource() == null) {
        throw new RuntimeException("No response body, error not in RFC7644: " + response.getResponseBody());
      } else if (response.getErrorResponse() == null) {
        checkAlreadyCreatedResource(response.getResource());
      } else {
        throw new BadRequestException("Bad request: " + response.getErrorResponse());
      }
    }
  }

  public <T extends ResourceNode> List<T> handleListResources(ServerResponse<ListResponse<T>> response)
      throws BadRequestException {
    if (response.isSuccess()) {
      outputUtils.logMsg(LOGGER, Logger.Level.INFO, GET_MESSAGE);
      if (response.getResource().getListedResources().isEmpty())
        System.out.println(EMPTY_MESSAGE);
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
    System.err.println("cannot create resources, already created : " + idList.toString());
  }

  private <T extends ResourceNode> void checkAlreadyCreatedResource(BulkResponse serverResponse)
      throws BadRequestException {
    var detailList = new ArrayList<String>();

    var operations = serverResponse.get("Operations");
    if (operations.isArray() && !operations.isNull()) {
      for (var item : operations) {
        detailList.add(item.get("response").get("detail").toString());
      }
    }
    detailList.forEach(detail -> {
      System.err.println(detail.toString());
    });
  }

  private <T extends ResourceNode> void checkAlreadyCreatedResource(ServerResponse<?> serverResponse) {
    if (serverResponse.getHttpStatus() == 409) {
      throw new BadRequestException("response error : cannot create resource, already created : "
          + serverResponse.getResource().get("id").toPrettyString());
    }
  }
}