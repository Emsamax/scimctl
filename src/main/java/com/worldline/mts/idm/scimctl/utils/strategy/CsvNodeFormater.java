package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

/**
 * concrete strategy for array in csv files
 */
public class CsvNodeFormater implements Strategy {
  private final ObjectMapper mapper;

  public CsvNodeFormater(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void handleFromat(ObjectNode curentNode, String key, String value) throws JsonProcessingException {
    if (isArray(key)) {
      handleArray(curentNode, key, value);
    } else if (key.split("\\.").length == 1) {
      // if key is not a composite
      var composite = key.split("\\.");
      var jsonNode = mapper.readTree("\"" + value + "\"");
      curentNode.set(composite[0], jsonNode);
    } else {
      var composite = key.split("\\.");
      // value is the last element of the composite
      // iterate on all the part of the key to create if not exist then set the value
      for (int i = 0; i < composite.length; i++) {
        var part = composite[i];
        if (i == composite.length - 1) {
          var jsonNode = mapper.readTree("\"" + value + "\"");
          curentNode.set(part, jsonNode);
        } else if (!curentNode.has(part) || !curentNode.get(part).isObject()) {
          var child = mapper.createObjectNode();
          curentNode.set(part, child);
          curentNode = child;
        } else {
          curentNode = (ObjectNode) curentNode.get(part);
        }
      }
    }
  }

  public void handleArray(ObjectNode currentObjectNode, String key, String value) {
    var composite = key.replace("*", "").trim().split("\\.");
    var keyName = composite[0];
    var items = value.split(";");
    var arrayNode = mapper.createArrayNode();
    for (String item : items) {
      var objNode = mapper.createObjectNode();
      String[] fields = item.trim().split(",");
      // split("=") -> 2 elements
      for (String s : fields) {
        var content = s.split("=");
        // check if value in array is bool to not have "true" but true
        if (Boolean.parseBoolean(content[1])) {
          handleBool(objNode, content[0], content[1]);
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

  private void handleBool(ObjectNode currentNode, String key, String value) {
    if (Boolean.parseBoolean(value)) {
      boolean bool = Boolean.parseBoolean(value);
      var boolNode = JsonNodeFactory.instance.booleanNode(bool);
      currentNode.set(key, boolNode);
    }
  }
}
