package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.utils.JsonUtils;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jboss.logging.Logger;


import java.io.IOException;

//TO NOT DO : manages duplicate user pas à moi de le faire

@ApplicationScoped
public class CreateService {

  @Inject
  ObjectMapper mapper;

  @Inject
  Logger LOGGER;

  @Named("requestUtils")
  @Inject
  RequestUtils requestUtils;

  @Inject
  JsonUtils jsonUtils;

  /**
   * @param data  contains the data you want to create. The data must be in JSON format.
   * @param clazz is the Object.Class you want to create from the data. User.class or Group.Class for example.
   */
  public <T extends ResourceNode> void createResource(String data, Class<T> clazz) throws IOException, IllegalArgumentException {
    String reformattedData = reformate(data);
    LOGGER.info("Reformated JSON: " + reformattedData);

    JsonNode actualObj = mapper.readTree(reformattedData);
    LOGGER.info("JSON parsed : " + actualObj.toString());
    LOGGER.info("pretty : " + actualObj.toPrettyString());


    if (actualObj.isArray() && actualObj.size() > 0) {

      String innerJsonStrinf = actualObj.get(0).asText();
      LOGGER.info("first element of array: " + innerJsonStrinf);

      JsonNode userNode = mapper.readTree(innerJsonStrinf);
      LOGGER.info("content of the element : " + userNode.toString());

      if (userNode.has("userName")) {
        JsonNode userNameNode = userNode.get("userName");
        if (userNameNode != null && userNameNode.isTextual()) {
          LOGGER.info("userName: " + userNameNode.asText());
        } else {
          LOGGER.info("No userName Found");
        }
      } else {
        LOGGER.info("No userName key");
      }
    } else {
      LOGGER.info("Json not an array or empty array provided");
    }

    requestUtils.createResource(actualObj, clazz);
    /*
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
        //var str = reformate(node.get(0).asText());
        var name = node.get(0).asText();
        LOGGER.info("Name : {}", name);
        LOGGER.info("Array element: {}", node.toPrettyString());
      } else {
        throw new IllegalArgumentException("Empty array provided");
      }
    }
    LOGGER.info("Node type: {}", node.getClass().getName());
    LOGGER.info("Node raw content: {}", node);
    LOGGER.info("Available fields: {}", node.fieldNames().toString());
    requestUtils.createResource(node, clazz);
     */
}

  private String reformate(String data) {
    if (!data.contains("\"") && data.contains(":")) {
      //remplace TEST par "TEST":
      data = data.replaceAll("([\\w]+)\\s*:", "\"$1\":");
      data = data.replaceAll(":\\s*([\\w]+)", ": \"$1\"");
    }
    LOGGER.info("Reformatted JSON: "+ data);
    return data;
  }
}
