package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jboss.logging.Logger;

/**
 * Context class and client class at the same time
 * -> contains the reference to the selected strategy (context)
 * -> Use a strategyResolver to change the strategy dynamically (client)
 * + handle the logic for serialize basic flat node
 */
public class NodeFormater {

  private final JsonNodeFormater jsonNodeFormater;
  private final CsvNodeFormater csvNodeFormater;

  private Strategy currentStrategy;

  private final StrategyResolver strategyResolver;

  private final ObjectMapper mapper;

  private final static Logger LOGGER = Logger.getLogger(NodeFormater.class);

  public NodeFormater(ObjectMapper mapper) {
    this.mapper = mapper;
    this.csvNodeFormater = new CsvNodeFormater(mapper);
    this.jsonNodeFormater = new JsonNodeFormater(mapper);
    this.strategyResolver = new StrategyResolver(csvNodeFormater, jsonNodeFormater);
    this.currentStrategy = this.csvNodeFormater;
  }

  public void setStrategy(Strategy newStrategy) {
    if(newStrategy == null){
      this.currentStrategy = csvNodeFormater;
      return;
    }
    this.currentStrategy = newStrategy;
  }

  public JsonNode flatToNestedNode(JsonNode flatNode) {
    LOGGER.info("FLAT : \n" + flatNode.toPrettyString());
    ObjectNode nestedNode = mapper.createObjectNode();
    flatNode.fields().forEachRemaining(field -> {
      var key = field.getKey();
      //ignore null fields name
      var value = field.getValue();
      if (value.isNull() || value.asText().isEmpty()) {
        return;
      }
       setStrategy(strategyResolver.resolveStrategyFromNode(key, value));
      if (this.currentStrategy == null) {
        LOGGER.error("Strategy was not resolved properly for key: " + key);
      }
      this.currentStrategy.handleFromat(nestedNode, key, value);
    });
    LOGGER.info("NESTED : \n" + nestedNode.toPrettyString());
    return nestedNode;
  }
}
