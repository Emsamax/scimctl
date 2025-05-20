package com.worldline.mts.idm.scimctl.common.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;

import java.io.IOException;

public class ResourceNodeDeserializer<T> extends StdDeserializer<T> {
  private final Class<T> clazz;

  public ResourceNodeDeserializer(Class<T> clazz) {
    super(clazz);
    this.clazz = clazz;
  }

  @Override
  public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    JsonNode node = p.getCodec().readTree(p);
    return new ObjectMapper().readValue(node.asText(), clazz);
  }
}


