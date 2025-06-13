package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeWrapper;

import org.apache.commons.csv.CSVFormat;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public class ResourceStreamBuilder {

  private File file;

  private Stream<NodeWrapper> currentStream;

  private final NodeFormater formater;

  private Map<String, Integer> header = new HashMap<>();

  public ResourceStreamBuilder(NodeFormater formater) {
    this.formater = formater;
  }

  public ResourceStreamBuilder fromFile(File file) {
    this.file = file;
    return this;
  }

  public File getFile() {
    return this.file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public Map<String, Integer> getHeader() {
    return this.header;
  }

  /**
   * @return stream of jsonNode.
   */
  public ResourceStreamBuilder build() throws IOException {
    if (this.file == null) {
      throw new IllegalStateException("File must be set before build()");
    }
    Reader in = new FileReader(this.file);
    var records = CSVFormat.RFC4180
        .builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .get()
        .parse(in);
    var iterator = records.iterator();
    this.header = records.getHeaderMap();
    this.currentStream = Stream.generate(() -> {
      if (iterator.hasNext()) {
        return new NodeWrapper(iterator.next());
      }
      return null;
    }).takeWhile(Objects::nonNull);
    return this;
  }

  public ResourceStreamBuilder convert() {
    if (this.currentStream == null) {
      throw new IllegalStateException("call build before convert");
    }
    this.currentStream = this.currentStream.map(wrapper -> {
      if (wrapper.getCsvRecord().isPresent()) {
        return formater.flatToNestedNode(wrapper, this.header);
      }
      throw new NoSuchElementException("CsvRecord cannot be empty for convert");
    });

    return this;
  }

  /**
   * Create as many chunk as possible with the specified chunk size
   */
  public Collection<List<NodeWrapper>> chunk(int size) {
    if (this.currentStream == null) {
      throw new IllegalStateException("Chunk must be called after build and convert");
    }
    var counter = new AtomicInteger();
    return this.currentStream
        .collect(groupingBy(x -> counter.getAndIncrement() / size))
        .values();
  }

}
