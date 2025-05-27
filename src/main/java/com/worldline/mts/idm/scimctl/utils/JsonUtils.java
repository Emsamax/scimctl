package com.worldline.mts.idm.scimctl.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;


@ApplicationScoped
@Unremovable
public class JsonUtils {

  @Inject
  ObjectMapper mapper;

  @Inject
  Logger LOGGER;

  public JsonNode flatToNestedNode(JsonNode flatNode, CsvSchema schema) {
    LOGGER.info("flat : \n" + flatNode.toPrettyString());
    ObjectNode nestedNode = mapper.createObjectNode();
    flatNode.fields().forEachRemaining(field -> {
      var key = field.getKey();
      //ignore null fields name
      var value = field.getValue();
      if (value.isNull() || value.asText().isEmpty()) {
        return;
      }
      ObjectNode currentNode = nestedNode;
      if (isArray(key)) {
        var composite = key.replace("*", "").trim().split("\\.");
        handleArray(currentNode, composite, value);
      } else if (key.split("\\.").length == 1) {
        //if key is not a composite
        var composite = key.split("\\.");
        currentNode.set(composite[0], value);
      } else {
        var composite = key.split("\\.");
        //value is the last element of the composite
        //iterate on all the part of the key to create if not exist then set the value
        for (int i = 0; i < composite.length; i++) {
          var part = composite[i];
          if (i == composite.length - 1) {
            currentNode.set(part, value);
          } else if (!currentNode.has(part) || !currentNode.get(part).isObject()) {
            var child = mapper.createObjectNode();
            currentNode.set(part, child);
            currentNode = child;
          } else {
            currentNode = (ObjectNode) currentNode.get(part);
          }
        }
      }
    });
    LOGGER.info("NESTED : \n" + nestedNode.toPrettyString());
    return nestedNode;
  }

  private void handleArray(ObjectNode currentObjectNode, String[] composite, JsonNode value) {
    String keyName = composite[0];
    String[] items = value.asText().split(";");
    var arrayNode = mapper.createArrayNode();
    for (String item : items) {
      var objNode = mapper.createObjectNode();
      String[] fields = item.trim().split(",");
      //split("=") -> 2 elements
      for (String s : fields) {
        var content = s.split("=");

        //check if value in array is bool to not have "true" but true
        if(isBoolFromText(content[1])){
          objNode.set(content[0], BooleanNode.valueOf(Boolean.parseBoolean(content[1])));
        }
        objNode.set(content[0], TextNode.valueOf(content[1]));
      }
      arrayNode.add(objNode);
    }
    currentObjectNode.set(keyName, arrayNode);
  }

  private boolean isArray(String s) {
    return s.startsWith("*");
  }

  private boolean isBoolFromText(String node){
    return Boolean.parseBoolean(node);
  }
}


