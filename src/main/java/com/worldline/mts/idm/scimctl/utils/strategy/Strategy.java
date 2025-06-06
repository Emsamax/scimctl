package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.csv.CSVRecord;

public interface Strategy {
  void handleFromat(ObjectNode curentNode, String key, String value) throws JsonProcessingException;
}
