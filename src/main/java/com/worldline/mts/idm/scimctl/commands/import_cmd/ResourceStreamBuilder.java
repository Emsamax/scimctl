package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.worldline.mts.idm.scimctl.utils.strategy.CsvNodeFormater;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;


public class ResourceStreamBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStreamBuilder.class);

  private File file;

  private Stream<JsonNode> currentStream;

  private final CsvMapper mapper;

  private final NodeFormater formater;

  public ResourceStreamBuilder(CsvMapper mapper, NodeFormater formater) {
    this.mapper = mapper;
    this.formater = formater;
  }

  public ResourceStreamBuilder fromFile(File file) {
    this.file = file;
    return this;
  }

  public File getFile(){
    return this.file;
  }

  public void setFile(File file){
    this.file = file;
  }

  /**
   * @return stream of jsonNode.
   */
  public ResourceStreamBuilder build() throws IOException {
    if (this.file == null) {
      throw new IllegalStateException("File must be set before build()");
    }
    CsvSchema schema = CsvSchema.builder()
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
    this.currentStream = this.currentStream.map(formater::flatToNestedNode);
    return this;
  }

  /**
   * Create as many chunk as possible with the specified chunk size
   */
  public Collection<List<JsonNode>> chunk(int chunkSize) {
    if (this.currentStream == null) {
      throw new IllegalStateException("Chunk must be called after build and convert");
    }
    var counter = new AtomicInteger();
    return this.currentStream.collect(groupingBy(x -> counter.getAndIncrement() / chunkSize)).values();
  }
}
