package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.NotFoundException;

public class StrategyResolver {
  private final CsvNodeFormater csvNodeFormater;
  private final JsonNodeFormater jsonNodeFormater;

  public StrategyResolver(CsvNodeFormater csvNodeFormater, JsonNodeFormater jsonNodeFormater) {
    this.csvNodeFormater = csvNodeFormater;
    this.jsonNodeFormater = jsonNodeFormater;
  }

  public Strategy resolveStrategyFromNode(String key, JsonNode node) throws NotFoundException {
    if (key.contains("*"))
      return this.csvNodeFormater;
    if (node.asText().contains("{") || node.asText().contains("[") || node.asText().contains(":"))
      return this.jsonNodeFormater;
    return null;
  }
}
