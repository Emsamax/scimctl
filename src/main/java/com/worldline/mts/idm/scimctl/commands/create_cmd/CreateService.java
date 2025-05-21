package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.worldline.mts.idm.scimctl.utils.JsonUtils;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.constants.ResourceTypeNames;
import de.captaingoldfish.scim.sdk.common.constants.enums.HttpMethod;
import de.captaingoldfish.scim.sdk.common.exceptions.BadRequestException;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.resources.multicomplex.Member;
import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonString;
import org.apache.logging.log4j.spi.ObjectThreadContextMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TO NOT DO : manages duplicate user pas à moi de le faire
@ApplicationScoped
public class CreateService {

  @Inject
  RequestUtils utils;

  @Inject
  ObjectMapper mapper;

  @Inject
  JsonUtils jsonUtils;

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateService.class);
  @Named("requestUtils")
  @Inject
  RequestUtils requestUtils;

  /**
   * @param data  contains the data you want to create. The data must be in JSON format.
   * @param clazz is the Object.Class you want to create from the data. User.class or Group.Class for example.
   */
  public <T extends ResourceNode> void createResource(String data, Class<T> clazz) throws IOException, IllegalArgumentException {
    LOGGER.info("Input data: {}", data);
    var jsonData = reformate(data);
    var node = mapper.readTree(jsonData);
    LOGGER.info("Initial parsed node: {}", node.toPrettyString());
    if (node.isTextual()) {
      node = mapper.readTree(node.asText());
      LOGGER.info("After text parsing: {}", node.toPrettyString());
    }
    if (node.isArray()) {
      if (!node.isEmpty()) {
        var str = reformate(node.get(0).asText());
        var newNode = mapper.readTree(str);
        LOGGER.info("New node: {}", newNode.get("userName"));
        LOGGER.info("Array element: {}", node.toPrettyString());
      } else {
        throw new IllegalArgumentException("Empty array provided");
      }
    }
    LOGGER.info("Node type: {}", node.getClass().getName());
    LOGGER.info("Node raw content: {}", node);
    LOGGER.info("Available fields: {}", node.fieldNames().toString());
    requestUtils.createResource(node, clazz);
  }

  private String reformate(String data) {
    if (data == null || data.trim().isEmpty()) {
      throw new IllegalArgumentException("Data cannot be empty");
    }
    String jsonData = data.trim();

    if (jsonData.startsWith("\"") && jsonData.endsWith("\"")) {
      jsonData = jsonData.substring(1, jsonData.length() - 1);
    }

    if (jsonData.contains("\\\"")) {
      jsonData = jsonData.replace("\\\"", "\"");
    }

    if (!jsonData.contains("\"") && jsonData.contains(":")) {
      //remplace test par "test":
      jsonData = jsonData.replaceAll("([\\w]+)\\s*:", "\"$1\":");
      jsonData = jsonData.replaceAll(":\\s*([\\w]+)", ": \"$1\"");
    }
    LOGGER.info("Reformatted JSON: {}", jsonData);
    return jsonData;
  }
}
