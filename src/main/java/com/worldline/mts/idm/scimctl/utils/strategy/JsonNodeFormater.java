package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jboss.logging.Logger;

/**
 * concrete strategy for json in csv files
 */
public class JsonNodeFormater implements Strategy {
  private final static Logger LOGGER = Logger.getLogger(JsonNodeFormater.class);
  private final ObjectMapper mapper;

  public JsonNodeFormater(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void handleFromat(ObjectNode curentNode, String key, JsonNode value) {
    if (isJson(value)) {
      handleJson(curentNode, key, value);
    } else if (key.split("\\.").length == 1) {
      //if key is not a composite
      var composite = key.split("\\.");
      curentNode.set(composite[0], value);
    } else {
      var composite = key.split("\\.");
      //value is the last element of the composite
      //iterate on all the part of the key to create if not exist then set the value
      for (int i = 0; i < composite.length; i++) {
        var part = composite[i];
        if (i == composite.length - 1) {
          curentNode.set(part, value);
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

  public boolean isJson(JsonNode value) {
    return (value.asText().contains("{") || value.asText().contains("["));
  }

  public void handleJson(ObjectNode curentNode, String key, JsonNode value) {
    curentNode.set(key, value);
  }
}
