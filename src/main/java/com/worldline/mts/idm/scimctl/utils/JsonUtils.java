package com.worldline.mts.idm.scimctl.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Unremovable
public class JsonUtils {

  @Inject
  ObjectMapper mapper;

  public JsonNode flatToNestedNode(JsonNode flatNode) {
    ObjectNode nestedNode = mapper.createObjectNode();

    flatNode.fields().forEachRemaining(field -> {
      var key = field.getKey();

      //ignore null fields name
      var value = field.getValue();
      if (value.isNull() || value.asText().equals("")) {
        return;
      }
      ObjectNode currentNode = nestedNode;
      String[] composite = key.split("/");
      //if key is not a composite
      if (composite.length == 1) {
        currentNode.set(composite[0], value);
      } else {
        //value is the last element of the composite
        //iterate on all the part of the key to create if not exist then set the value
        for (int i = 0; i < composite.length; i++) {
          var part = composite[i];
          if (i == composite.length - 1) {
            currentNode.set(part, value);
          } else if (!currentNode.has(part) || !currentNode.get(part).isObject()) {
            var child = mapper.createObjectNode();
            currentNode.set(part, child);
            currentNode = child;
          } else {
            currentNode = (ObjectNode) currentNode.get(part);
            //currentNode.set(part, value);
          }
        }
      }
    });
    System.out.println("NESTED : \n" + nestedNode.toPrettyString());
    return nestedNode;
  }
}
