package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.csv.CSVRecord;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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
    if (newStrategy == null) {
      this.currentStrategy = csvNodeFormater;
      return;
    }
    this.currentStrategy = newStrategy;
  }

  public NodeWrapper flatToNestedNode(NodeWrapper nodeWrapper, Map<String, Integer> header) {
    var csvRecord = nodeWrapper.getCsvRecord();
    ObjectNode nestedNode = mapper.createObjectNode();
    for (Map.Entry<String, Integer> entry : header.entrySet()) {
      var key = entry.getKey();
      //ignore null fields name
      var pos = entry.getValue();
      if (!(key == null || key.isEmpty())) {
        setStrategy(strategyResolver.resolveStrategyFromNode(key, csvRecord.get().get(pos)));
        if (this.currentStrategy == null) {
          LOGGER.error("Strategy was not resolved properly for key: " + key + " values : " + csvRecord);
        }
        try {
          this.currentStrategy.handleFromat(nestedNode, key, csvRecord.get().get(pos));
        } catch (JsonProcessingException e) {
          LOGGER.error(e.getMessage());
        }
      }
    }
    LOGGER.info("NESTED : \n" + nestedNode.toPrettyString());
    nodeWrapper.setJsonNode(nestedNode);
    return nodeWrapper;
  }
}
