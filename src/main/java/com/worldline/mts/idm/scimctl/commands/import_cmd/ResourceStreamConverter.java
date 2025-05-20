package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ApplicationScoped
@Unremovable
public class ResourceStreamConverter{

  @Inject
  ObjectMapper mapper;



  public <T extends ResourceNode> Stream<JsonNode> convert(Stream<JsonNode> nodes, Class<T> clazz) {
    return nodes.map(flatNode -> {
        var nestedNode = flatToNestedNode(flatNode);
        //System.out.println(nestedNode.toPrettyString());
        return nestedNode;
    });
  }

  private JsonNode flatToNestedNode(JsonNode flatNode) {
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

  Stream<List<JsonNode>>chunk(Stream<JsonNode> nestedJsonNode, int chunkSize) {
    Iterator<JsonNode> iterator = nestedJsonNode.iterator();
    Iterator<List<JsonNode>> chunkIterator = new Iterator<>() {

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public List<JsonNode> next() {
        List<JsonNode> chunk = new ArrayList<>();
        for (int i = 0; i < chunkSize && iterator.hasNext(); i++) {
          chunk.add(iterator.next());
        }
        return chunk;
      }
    };
    return StreamSupport.stream(((Iterable<List<JsonNode>>) () -> chunkIterator).spliterator(), false);
  }

}
