package com.worldline.mts.idm.scimctl.utils;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeWrapper;

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
import de.captaingoldfish.scim.sdk.common.response.ListResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Named("requestUtils")
@Unremovable
@ApplicationScoped

public class RequestUtils {

  private ScimRequestBuilder requestBuilder;

  @Inject
  ClientConfig config;

  String baseUrl;

  public RequestUtils(ClientConfig config,@ConfigProperty(name = "base.url") String baseUrl) {
    this.config = config;
    this.baseUrl = baseUrl;
    this.requestBuilder = new ScimRequestBuilder(baseUrl, this.config.getScimClientConfig());
  }

  @Inject
  ServerResponseHandler responseHandler;

  public <T extends ResourceNode> T getResource(String id, Class<T> clazz)
      throws BadRequestException, IllegalArgumentException, ClassCastException {
    var path = getEndPointPath(clazz);
    var response = this.requestBuilder.get(User.class, path, id).sendRequest();
    return (T) responseHandler.handleServerResponse(response, ServerResponseHandler.GET_MESSAGE);
  }

  public <T extends ResourceNode> List<T> getResources(Class<T> clazz) {
    if (!isUser(clazz) && !isGroup(clazz)) {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }
    var path = getEndPointPath(clazz);
    var response = this.requestBuilder
        .list(clazz, path)
        .count(50)
        .get()
        .sendRequest();
    return responseHandler.handleListResources(response);
  }

  public <T extends ResourceNode> List<T> getFilteredResources(Class<T> clazz, String filter) {
    if (!isUser(clazz) && !isGroup(clazz)) {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }
    var path = getEndPointPath(clazz);
    ServerResponse<ListResponse<T>> response = this.requestBuilder
        .list(clazz, path)
        .filter("userName", Comparator.CO, filter)
        .build()
        .post()
        .getAll();
    return responseHandler.handleListResources(response);
  }

  public <T extends ResourceNode> void createResource(JsonNode node, Class<T> clazz) {
    var path = getEndPointPath(clazz);
    try (var requestBuilder = this.requestBuilder) {
      if (!node.has("userName") || node.get("userName").asText().isEmpty()) {
        throw new RuntimeException("Resource must have a userName : " + node);
      }
      var response = requestBuilder.create(clazz, path).setResource(node).sendRequest();
      responseHandler.handleServerResponse(response, ServerResponseHandler.CREATE_MESSAGE);
    }
  }

  /**
   * Creates a bulk SCIM resource request based on a List of resources and a
   * specified resource type.
   * The method builds bulk operations, and sends them to the server.
   *
   * @param <T>   the type of resources to be processed
   * @param chunk a list of resources to be included in the bulk request(50 max)
   * @param clazz the class type of the resource (e.g., User.class or Group.class)
   *              used to determine endpoint paths
   * @throws RuntimeException   if a resource does not contain a userName or if no
   *                            response body is received
   * @throws ClassCastException if the provided class type is neither User nor
   *                            Group
   */
  public <T extends JsonNode> void createResources(List<NodeWrapper> chunk, Class<T> clazz) {
    String path = getEndPointPath(clazz);
    String resourceType = getResourceType(clazz);
    var scimRequestBuilder = this.requestBuilder;
    var builder = scimRequestBuilder.bulk();
    List<Member> groupMembers = new ArrayList<>();

    try {
      for (NodeWrapper wrapper : chunk) {
        var node = wrapper.getJsonNode().get();
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
      responseHandler.handleBulkResponse(response);
    } catch (NoSuchElementException e) {
      e.getMessage();
    }

  }

  public <T extends ResourceNode> void updateResource(String id, Class<T> clazz, JsonNode updatedData) {
    var path = getEndPointPath(clazz);
    var response = this.requestBuilder.update(clazz, path, id)
        .setResource(updatedData)
        .sendRequest();
    responseHandler.handleServerResponse(response, ServerResponseHandler.UPDATE_MESSAGE);
  }

  public <T extends ResourceNode> void deleteResource(String id, Class<T> clazz) {
    ServerResponse<User> response = this.requestBuilder.delete(User.class, getEndPointPath(clazz), id)
        .sendRequest();
    responseHandler.handleServerResponse(response, ServerResponseHandler.DELETE_MESSAGE);
  }

  /**
   * Determines the endpoint path based on the provided resource class type.
   *
   * @param <T>   the type of the resource class
   * @param clazz the class type to determine the corresponding endpoint path
   *              (e.g., User.class or Group.class)
   * @return the endpoint path as a string, corresponding to the specified
   *         resource class
   * @throws ClassCastException if the provided class is neither User nor Group
   */
  private <T> String getEndPointPath(Class<T> clazz) throws ClassCastException {
    String endpointPath;
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
   * @param clazz the class type to determine the resource type (e.g., User.class
   *              or Group.class)
   * @return the resource type as a string, represented by constants such as
   *         ResourceTypeNames.USER or ResourceTypeNames.GROUPS
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
