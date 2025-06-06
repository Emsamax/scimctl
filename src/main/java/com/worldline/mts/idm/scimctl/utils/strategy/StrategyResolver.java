package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.NotFoundException;
import org.apache.commons.csv.CSVRecord;
import org.jboss.logging.Logger;

public class StrategyResolver {
  private final CsvNodeFormater csvNodeFormater;
  private final JsonNodeFormater jsonNodeFormater;
  private final static Logger LOGGER = Logger.getLogger(JsonNodeFormater.class);

  public StrategyResolver(CsvNodeFormater csvNodeFormater, JsonNodeFormater jsonNodeFormater) {
    this.csvNodeFormater = csvNodeFormater;
    this.jsonNodeFormater = jsonNodeFormater;
  }

  public Strategy resolveStrategyFromNode(String key, String record) throws NotFoundException {
    if (record.contains("{") || record.contains("[") || record.contains(":")) {
      LOGGER.info("Strategy JsonNodeFormater");
      return this.jsonNodeFormater;
    } else if (key.contains("*")) {
           LOGGER.info("Strategy CsvNodeFormater");
           return this.csvNodeFormater;
    } else return this.csvNodeFormater;
  }
}
