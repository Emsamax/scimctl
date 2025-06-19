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
      if (!isEmptyResponse(response))
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
    // checkAlreadyCreatedResource(serverResponse);
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

  @Inject
  Reporting reporting;

  /**
   * If bulk different handle
   * @param response
   */
  public void handleBulkResponse(ServerResponse<BulkResponse> response) {
    if (response.isSuccess()) {
      var bulkResponse = response.getResource();
      if (bulkResponse.isEmpty()) {
        System.out.println(EMPTY_MESSAGE);
      }
      reporting.report(bulkResponse);
    } else if (response.getErrorResponse() == null && response.getResource() == null) {
      throw new RuntimeException("No response body, error not in RFC7644: " + response.getResponseBody());
    } else if (response.getErrorResponse() == null) {

    } else {
      throw new BadRequestException("Bad request: " + response.getErrorResponse());
    }

  }

  public <T extends ResourceNode> Optional<List<T>> handleListResources(ServerResponse<ListResponse<T>> response) {
    if (response.isSuccess()) {
      outputUtils.logMsg(GET_MESSAGE);
      if (response.getResource().get("totalResults").asInt() == 0) {
        System.out.println(EMPTY_MESSAGE);
        return Optional.empty();
      }
      return Optional.of(response.getResource().getListedResources());
    }
    if (response.getErrorResponse() == null) {
      checkAlreadyCreatedResource(response.getResource());
      System.err.println(
          "Invalid response format: " + "\nbody :" + response.getResponseBody() + ",\nerror : "
              + response.getErrorResponse() + ",\nstatus: " + response.getHttpStatus() + ",\nheader : "
              + response.getHttpHeaders() + ",\ninfo : ");
      return Optional.empty();
    }
    System.err.println("Bad request : " + response.getErrorResponse().toPrettyString());
    return Optional.empty();
  }

  private <T extends ResourceNode> void checkAlreadyCreatedResource(ListResponse<?> serverResponse)
      throws BadRequestException {
    var idList = new ArrayList<String>();
    for (var resp : serverResponse.getListedResources()) {
      idList.add(resp.get("id").asText());
    }
    System.err.println("cannot create resources, already created : " + idList.toString());
  }

  private <T extends ResourceNode> void checkAlreadyCreatedResource(ServerResponse<?> serverResponse) {
    if (serverResponse.getHttpStatus() == 409) {
      throw new BadRequestException("response error : cannot create resource, already created : "
          + serverResponse.getResource().get("id").toPrettyString());
    }
  }

  private <T extends ResourceNode> boolean isEmptyResponse(ServerResponse<T> response) {
    var resource = response.getResource();
    System.out.println(resource);
    if (resource instanceof List) {
      if (resource.get("totalResults").asInt() == 0) {
        System.out.println(EMPTY_MESSAGE);
        return true;
      }
      return false;
    } else {
      if (resource.get("id") == null) {
        System.out.println(EMPTY_MESSAGE);
        return true;
      }
      return false;
    }
  }

}