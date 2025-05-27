package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.worldline.mts.idm.scimctl.utils.JsonUtils;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Unremovable
@ApplicationScoped
public class ResourceStreamBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStreamBuilder.class);

  @Inject
  CsvMapper mapper;

  @Inject
  JsonUtils jsonUtils;

  private File file;

  private Stream<JsonNode> currentStream;

  private CsvSchema schema;

  public CsvSchema getSchema(){
    return this.schema;
  }

  public ResourceStreamBuilder fromFile(File file) {
    this.file = file;
    return this;
  }

  /**
   * @return stream of jsonNode.
   */
  public ResourceStreamBuilder build() throws IOException {
    if (this.file == null) {
      throw new IllegalStateException("File must be set before build()");
    }
     this.schema = CsvSchema.builder()
      .setUseHeader(true)
      .setColumnSeparator(',')
      .setQuoteChar('"')
      .setArrayElementSeparator(";")
      .build();
    var iterator = mapper
      .enable(CsvParser.Feature.SKIP_EMPTY_LINES)
      .enable(CsvParser.Feature.TRIM_SPACES)
      .enable(CsvParser.Feature.WRAP_AS_ARRAY)
      .readerFor(JsonNode.class)
      .with(schema)
      .readValues(this.file);
    this.currentStream = Stream.generate(() -> {
        if (iterator.hasNext()) {
          return (JsonNode) iterator.next();
        }
        return null;
      })
      .takeWhile(Objects::nonNull)
      .onClose(() -> {
        try {
          iterator.close();
        } catch (IOException e) {
          LOGGER.error("Error while closing mapper : `{}`", e.getMessage());
          throw new UncheckedIOException(e);
        }
      });
    return this;
  }

  public ResourceStreamBuilder convert() {
    if (this.currentStream == null) {
      throw new IllegalStateException("call build before convert");
    }
    this.currentStream = this.currentStream.map(flatNode -> jsonUtils.flatToNestedNode(flatNode, schema));
    return this;
  }

  /**
   * Create as many chunk as possible with the specified chunk size
   * @param chunkSize
   * @return
   */
  public Collection<List<JsonNode>> chunk(int chunkSize) {
    if (this.currentStream == null) {
      throw new IllegalStateException("Chunk must be called after build and convert");
    }
    var counter = new AtomicInteger();
    return this.currentStream.collect(groupingBy(x -> counter.getAndIncrement() / chunkSize)).values();
  }
}
