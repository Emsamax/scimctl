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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.antlr.v4.parse.ANTLRParser.channelsSpec_return;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Named("requestUtils")
@Unremovable
@ApplicationScoped

public class RequestUtils {

  private ScimRequestBuilder requestBuilder;

  @Inject
  ClientConfig config;

  @Inject
  ServerResponseHandler responseHandler;

  @Inject
  OutputUtils outputUtils;

  String baseUrl;

  int batchSize;

  public RequestUtils(ClientConfig config, @ConfigProperty(name = "base.url") String baseUrl,
      @ConfigProperty(name = "batch.size") int batch_size) {
    this.batchSize = batch_size;
    this.config = config;
    this.baseUrl = baseUrl;
    this.requestBuilder = new ScimRequestBuilder(baseUrl, this.config.getScimClientConfig());
  }

  public <T extends ResourceNode> void getResource(String id, Class<T> clazz) {
    var path = getEndPointPath(clazz);
    if (!outputUtils.getDryRun()) {
      outputUtils.logMsg("get request : " + baseUrl + path + "/" + id);
      var response = this.requestBuilder.get(User.class, path, id).sendRequest();
      var result = responseHandler.handleServerResponse(response, ServerResponseHandler.GET_MESSAGE);
      if (result.isPresent()) {
        System.out.println(result.get().toPrettyString());
      }
    } else {
      outputUtils.logMsg("get request would be sent at : " + baseUrl + path + "/" + id);
    }
  }

  public void getSchemas() {
    var path = EndpointPaths.SCHEMAS;
    var response = this.requestBuilder
        .list(User.class, path)
        .count(batchSize)
        .get()
        .sendRequest();
    var result = responseHandler.handleListResources(response);
    if (result.isPresent()) {
      result.get().forEach(resp -> {
        System.out.println(resp.toPrettyString());
      });
    }
  }

  public <T extends ResourceNode> void getResources(Class<T> clazz) {
    if (!isUser(clazz) && !isGroup(clazz)) {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }
    var path = getEndPointPath(clazz);
    if (!outputUtils.getDryRun()) {
      outputUtils.logMsg("get request : " + baseUrl + path);
      var response = this.requestBuilder
          .list(clazz, path)
          .count(batchSize)
          .get()
          .sendRequest();
      var result = responseHandler.handleListResources(response);
      if (result.isPresent()) {
        result.get().forEach(resp -> {
          System.out.println(resp.toPrettyString());
        });
      }
    } else {
      outputUtils.logMsg("get request would be sent at : " + baseUrl + path);
    }
  }

  public <T extends ResourceNode> void getFilteredResources(Class<T> clazz, String filter) {
    if (!isUser(clazz) && !isGroup(clazz)) {
      throw new ClassCastException("Class is not User or Group : " + clazz.getName());
    }
    var path = getEndPointPath(clazz);
    if (!outputUtils.getDryRun()) {
      outputUtils.logMsg("get request : " + baseUrl + path + "/" + filter);
      ServerResponse<ListResponse<T>> response = this.requestBuilder
          .list(clazz, path)
          .filter("userName", Comparator.CO, filter)
          .build()
          .post()
          .getAll();
      var result = responseHandler.handleListResources(response);
      if (result.isPresent()) {
        result.get().forEach(resp -> {
          System.out.println(resp.toPrettyString());
        });
      }
    } else {
      outputUtils.logMsg("get request would be sent at : " + baseUrl + path + "/" + filter);
    }
  }

  public <T extends ResourceNode> void createResource(JsonNode node, Class<T> clazz) {
    var path = getEndPointPath(clazz);
    try (var requestBuilder = this.requestBuilder) {
      outputUtils.logMsg("create request : " + baseUrl + path);
      if (outputUtils.getDryRun()) {
        outputUtils.logMsg("create request would be sent at : " + baseUrl + path);
        return;
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
   * @param chunk a list of resources to be included in the bulk request(size of a
   *              chunk = batchSize, 50 by default)
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
        String bulkId = UUID.randomUUID().toString();
        builder.bulkRequestOperation(path).method(HttpMethod.POST).data(node).bulkId(bulkId).next();
        groupMembers.add(Member.builder().value("bulkId:" + bulkId).type(resourceType).build());
      }
      Group finalGroup = Group.builder().displayName("chunk-group-" + UUID.randomUUID()).members(groupMembers).build();
      if (!outputUtils.getDryRun()) {
        ServerResponse<BulkResponse> response = builder.bulkRequestOperation(EndpointPaths.GROUPS)
            .method(HttpMethod.POST)
            .bulkId(UUID.randomUUID().toString())
            .data(finalGroup)
            .sendRequest();
        outputUtils.logMsg("bulk request : " + baseUrl + path + EndpointPaths.GROUPS);
        responseHandler.handleBulkResponse(response);
      } else
        outputUtils.logMsg(
            "bulk request would be sent at: " + baseUrl + path + EndpointPaths.GROUPS);
    } catch (NoSuchElementException e) {
      e.getMessage();
    }

  }

  public <T extends ResourceNode> void updateResource(String id, Class<T> clazz, JsonNode updatedData) {
    var path = getEndPointPath(clazz);
    if (!outputUtils.getDryRun()) {
      outputUtils.logMsg("update request : " + baseUrl + path);
      System.out.println(updatedData);
      var response = this.requestBuilder.update(clazz, path, id)
          .setResource(updatedData)
          .sendRequest();
      responseHandler.handleServerResponse(response, ServerResponseHandler.UPDATE_MESSAGE);
    } else
      outputUtils.logMsg("update request would be sent at : " + baseUrl + path);

  }

  public <T extends ResourceNode> void deleteResource(String id, Class<T> clazz) {
    var path = getEndPointPath(clazz);
    if (!outputUtils.getDryRun()) {
      outputUtils.logMsg("delete request : " + baseUrl + path + "/" + id);
      ServerResponse<T> response = this.requestBuilder.delete(clazz, path, id)
          .sendRequest();
      responseHandler.handleServerResponse(response, ServerResponseHandler.DELETE_MESSAGE);
    } else
      outputUtils.logMsg("delete request would be sent at : " + baseUrl + path + "/" + id);
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
