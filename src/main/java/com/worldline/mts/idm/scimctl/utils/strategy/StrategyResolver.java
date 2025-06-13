package com.worldline.mts.idm.scimctl.utils.strategy;

import jakarta.ws.rs.NotFoundException;

public class StrategyResolver {
  private final CsvNodeFormater csvNodeFormater;
  private final JsonNodeFormater jsonNodeFormater;
  public StrategyResolver(CsvNodeFormater csvNodeFormater, JsonNodeFormater jsonNodeFormater) {
    this.csvNodeFormater = csvNodeFormater;
    this.jsonNodeFormater = jsonNodeFormater;
  }

  public Strategy resolveStrategyFromNode(String key, String record) throws NotFoundException {
    if (record.contains("{") || record.contains("[") || record.contains(":")) {
      return this.jsonNodeFormater;
    } else if (key.contains("*")) {
      return this.csvNodeFormater;
    } else
      return this.csvNodeFormater;
  }
}
