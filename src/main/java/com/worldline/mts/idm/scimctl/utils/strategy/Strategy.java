package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Strategy {
  void handleFromat(ObjectNode curentNode, String key, JsonNode value);
}
