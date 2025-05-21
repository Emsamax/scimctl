package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worldline.mts.idm.scimctl.utils.JsonUtils;
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

  @Inject
  JsonUtils jsonUtils;

  private Stream<JsonNode> currentStream;

  public ResourceStreamConverter convert() {
    if (this.currentStream == null) {
      throw new IllegalStateException("call build before convert");
    }
    this.currentStream = this.currentStream.map(flatNode -> jsonUtils.flatToNestedNode(flatNode));
    return this;
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
