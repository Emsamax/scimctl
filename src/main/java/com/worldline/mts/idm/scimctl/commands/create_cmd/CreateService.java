package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.utils.NodeFormater;
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
  NodeFormater nodeFormater;

  /**
   * @param data  contains the data you want to create. The data must be in JSON format.
   * @param clazz is the Object.Class you want to create from the data. User.class or Group.Class for example.
   */
  public <T extends ResourceNode> void createResource(String data, Class<T> clazz) throws IOException, IllegalArgumentException {
    LOGGER.infof("input data : %s", data);
    //String reformattedData = reformate(data);
    var myMapper = new ObjectMapper();
    data = data.replaceAll("\\\\", "");
    LOGGER.infof("cleaned data : %s", data);

    var resource = myMapper.readValue(data, JsonNode.class);
    LOGGER.info("JSON parsed : " + resource.toString());
    LOGGER.info("pretty : " + resource.toPrettyString());
    /*if (resource.isArray()) {
      String first = actualObj.get(0).asText();
      var node = mapper.readTree(first);
      LOGGER.info("reading table " + node.toPrettyString());
      JsonNode userNameNode = node.get("userName");
      if (userNameNode != null && !userNameNode.asText().isEmpty()) {
        LOGGER.info("userName : " + userNameNode.asText());
      } else {
        LOGGER.warn("No userName found or empty");
      }
    } else {
      LOGGER.info("JSON is not an array");
    }*/

    requestUtils.createResource(resource, clazz);
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
    LOGGER.info("Reformatted JSON: " + data);
    return data;
  }
}
