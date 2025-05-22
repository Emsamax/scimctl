package com.worldline.mts.idm.scimctl.utils;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.fasterxml.jackson.databind.JsonNode;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.constants.ResourceTypeNames;
import de.captaingoldfish.scim.sdk.common.constants.enums.Comparator;
import de.captaingoldfish.scim.sdk.common.constants.enums.HttpMethod;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.resources.multicomplex.Member;
import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import de.captaingoldfish.scim.sdk.common.response.ErrorResponse;
import de.captaingoldfish.scim.sdk.common.response.ListResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Named("requestUtils")
@Unremovable
@ApplicationScoped

//TODO : métohde de gestion des serverResponse
public class RequestUtils {

  @Inject
  ClientConfig config;

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

  public <T extends ResourceNode> T getResource(String id, Class<T> clazz) throws BadRequestException, IllegalArgumentException, ClassCastException {
    var path = getEndPointPath(clazz);
    var scimClientConfig = config.getScimClientConfig();
    var scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), scimClientConfig);
    var response = scimRequestBuilder.get(User.class, path, id).sendRequest();

    if (response.isSuccess()) {
      if (response.getResource() == null) {
        throw new BadRequestException("resource is null");
      }
      return castSucces(clazz, response);
    } else if (response.getErrorResponse() == null) {
      throw new BadRequestException(" response was not an error response as described in RFC7644  :" + response.getResponseBody());
    } else {
      throw new IllegalArgumentException("id does not exist" + response.getResponseBody());
    }
  }


  public <T extends ResourceNode> List<T> getResources(Class<T> clazz) {
    if (!isUser(clazz) && !isGroup(clazz)) {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }
    var path = getEndPointPath(clazz);
    var scimClientConfig = config.getScimClientConfig();
    var scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), scimClientConfig);
    ServerResponse<ListResponse<T>> response = scimRequestBuilder
      .list(clazz, path)
      .count(50)
      .get()
      .sendRequest();
    if (response.isSuccess()) {
      return response.getResource().getListedResources();
    }

    if (response.getErrorResponse() == null) {
      throw new BadRequestException("Invalid response format: " + response.getResponseBody());
    }
    throw new BadRequestException(response.getResponseBody());
  }

  public <T extends ResourceNode> List<T> getFilteredResources(Class<T> clazz, String filter) {
    if (!isUser(clazz) && !isGroup(clazz)) {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }
    var path = getEndPointPath(clazz);
    var scimClientConfig = config.getScimClientConfig();
    var scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), scimClientConfig);
    ServerResponse<ListResponse<T>> response = scimRequestBuilder
      .list(clazz, path)
      .filter("userName", Comparator.CO, filter)
      .build()
      .post()
      .getAll();

    if (response.isSuccess()) {
      return response.getResource().getListedResources();
    }

    if (response.getErrorResponse() == null) {
      throw new BadRequestException("Invalid response format: " + response.getResponseBody());
    }
    throw new BadRequestException(response.getResponseBody());
  }


  public <T extends ResourceNode> void createResource(JsonNode node, Class<T> clazz) {
    var path = getEndPointPath(clazz);
    try (var requestBuilder = new ScimRequestBuilder(config.getBaseUrl(), config.getScimClientConfig())) {
      if (!node.has("userName") || node.get("userName").asText().isEmpty()) {
        throw new RuntimeException("Resource must have a userName : " + node);
      }
      var response = requestBuilder.create(clazz, path).setResource(node).sendRequest();
      if (response.isSuccess()) {
        LOGGER.info("Resource created successfully : {}", response.getResource());
      } else if (response.getErrorResponse() == null && response.getResource() == null) {
        LOGGER.error("Error not if RFC7644 : {}", response.getResource());
        throw new RuntimeException("No response body, error not in RFC7644: " + response.getResponseBody());
      } else {
        LOGGER.error("Bad request: {}", response.getResource());
        throw new BadRequestException("Bad request: " + response.getErrorResponse());
      }
    }
  }

  /**
   * Creates a bulk SCIM resource request based on a stream of resources and a specified resource type.
   * The method validates resource attributes, builds bulk operations, and sends them to the server.
   *
   * @param <T>   the type of resources to be processed
   * @param chunk a list of resources to be included in the bulk request(50 max)
   * @param clazz the class type of the resource (e.g., User.class or Group.class) used to determine endpoint paths
   * @throws RuntimeException   if a resource does not contain a userName or if no response body is received
   * @throws ClassCastException if the provided class type is neither User nor Group
   */
  public <T extends JsonNode> void createResources(List<JsonNode> chunk, Class<T> clazz) {
    String path = getEndPointPath(clazz);
    String resourceType = getResourceType(clazz);
    var scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), config.getScimClientConfig());
    var builder = scimRequestBuilder.bulk();

    List<Member> groupMembers = new ArrayList<>();

    for (JsonNode node : chunk) {
      if (!node.has("userName") || node.get("userName").asText().isEmpty()) {
        throw new RuntimeException("Resource must have a userName for bulk request: " + node);
      }
      String bulkId = UUID.randomUUID().toString();
      builder.bulkRequestOperation(path).method(HttpMethod.POST).data(node).bulkId(bulkId).next();
      groupMembers.add(Member.builder().value("bulkId:" + bulkId).type(resourceType).build());
    }

    Group finalGroup = Group.builder().displayName("chunk-group-" + UUID.randomUUID()).members(groupMembers).build();
    ServerResponse<BulkResponse> response = builder.bulkRequestOperation(EndpointPaths.GROUPS)
      .method(HttpMethod.POST)
      .bulkId(UUID.randomUUID().toString())
      .data(finalGroup)
      .sendRequest();
    if (response.isSuccess()) {
      BulkResponse bulkResponse = response.getResource();
      LOGGER.info("Bulk Response: {}", bulkResponse);
      LOGGER.info("Failed Operations: {}", bulkResponse.getFailedOperations());
      LOGGER.info("Successful Operations: {}", bulkResponse.getSuccessfulOperations());
      LOGGER.info("HTTP Status: {}", bulkResponse.getHttpStatus());
    } else if (response.getErrorResponse() == null && response.getResource() == null) {
      throw new RuntimeException("No response body, error not in RFC7644: " + response.getResponseBody());
    } else if (response.getErrorResponse() == null) {
      throw new RuntimeException("Bulk error: " + response.getResponseBody());
    } else {
      throw new BadRequestException("Bad request: " + response.getErrorResponse());
    }

  }

  //TODO : update 1 resource
  public <T> void updateResource(String id, Class<T> t) {
  }

  public <T extends ResourceNode> void deleteResource(String id, Class<T> clazz) {
    var requestBuilder = new ScimRequestBuilder(config.getBaseUrl(), config.getScimClientConfig());
    ServerResponse<User> response = requestBuilder.delete(User.class, getEndPointPath(clazz), id)
      .sendRequest();
    if (response.isSuccess()) {
      var resp = response.getResource();
      LOGGER.info("User deleted successfully `{}`", resp);
    } else if (response.getErrorResponse() == null) {
      // the response was not an error response as described in RFC7644
      String errorMessage = response.getResponseBody();
      LOGGER.error("Error while deleting user: `{}`", errorMessage);
    } else {
      ErrorResponse errorResponse = response.getErrorResponse();
      LOGGER.error("Error `{}`", errorResponse);
    }
  }


  /**
   * Determines the endpoint path based on the provided resource class type.
   *
   * @param <T>   the type of the resource class
   * @param clazz the class type to determine the corresponding endpoint path (e.g., User.class or Group.class)
   * @return the endpoint path as a string, corresponding to the specified resource class
   * @throws ClassCastException if the provided class is neither User nor Group
   */
  private <T> String getEndPointPath(Class<T> clazz) throws ClassCastException {
    String endpointPath = "";
    if (clazz.equals(de.captaingoldfish.scim.sdk.common.resources.User.class)) {
      endpointPath = EndpointPaths.USERS;
    } else if (clazz.equals(de.captaingoldfish.scim.sdk.common.resources.Group.class)) {
      endpointPath = EndpointPaths.GROUPS;
    } else {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }
    return endpointPath;
  }

  /**
   * Casts the resource retrieved from a ServerResponse to the specified type if the type matches.
   *
   * @param <T>      the type to cast the resource to
   * @param clazz    the Class object representing the desired type
   * @param response the ServerResponse containing the resource to be cast
   * @return the resource cast to the specified type
   * @throws ClassCastException if the resource type does not match the specified type
   */
  private <T> T castSucces(Class<T> clazz, ServerResponse<?> response) throws ClassCastException {
    var result = (T) response.getResource();
    if (result.getClass().equals(clazz)) {
      return result;
    } else {
      throw new ClassCastException("Resource is not of type " + clazz.getName());
    }
  }

  /**
   * Check if the given class is a User
   *
   * @param clazz
   * @param <T>
   * @return
   */
  private <T> boolean isUser(Class<T> clazz) {
    return clazz.equals(User.class);
  }

  /**
   * Check if the given class is a Group
   *
   * @param clazz
   * @param <T>
   * @return
   */
  private <T> boolean isGroup(Class<T> clazz) {
    return clazz.equals(Group.class);
  }

  /**
   * Determines the resource type based on the provided class type.
   *
   * @param <T>   the type of the resource
   * @param clazz the class type to determine the resource type (e.g., User.class or Group.class)
   * @return the resource type as a string, represented by constants such as ResourceTypeNames.USER or ResourceTypeNames.GROUPS
   * @throws ClassCastException if the provided class is neither User nor Group
   */
  private <T> String getResourceType(Class<T> clazz) throws ClassCastException {
    if (isUser(clazz)) {
      return ResourceTypeNames.USER;
    } else if (isGroup(clazz)) {
      return ResourceTypeNames.GROUPS;
    } else {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }

  }
}


