package com.worldline.mts.idm.scimctl.utils.strategy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.csv.CSVRecord;
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
  public void handleFromat(ObjectNode curentNode, String key, String value) {
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

  public boolean isJson(String value) {
    return (value.contains("{") || value.contains("["));
  }

  public void handleJson(ObjectNode currentNode, String attrName, String value) throws JsonProcessingException {
    var jsonNode = mapper.readTree(value);
    currentNode.set(attrName, jsonNode);
  }

}
