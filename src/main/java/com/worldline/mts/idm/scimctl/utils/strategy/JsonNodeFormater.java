package com.worldline.mts.idm.scimctl.utils.strategy;
import com.fasterxml.jackson.core.JsonProcessingException;
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
      try {
        var composite = key.split("\\.");
        var attrName = composite[composite.length - 1];
        handleJson(curentNode, attrName, value);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public boolean isJson(JsonNode value) {
    return (value.asText().contains("{") || value.asText().contains("["));
  }

  public void handleJson(ObjectNode currentNode, String attrName, JsonNode value) throws JsonProcessingException {
    var jsonNode = mapper.readTree(value.asText());
    currentNode.set(attrName, jsonNode);
  }

}
