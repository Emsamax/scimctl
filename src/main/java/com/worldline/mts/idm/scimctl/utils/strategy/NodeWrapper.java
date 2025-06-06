package com.worldline.mts.idm.scimctl.utils.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.csv.CSVRecord;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Wrapper class for csvRecords
 */
public class NodeWrapper {
  private CSVRecord csvRecord;
  private JsonNode jsonNode;

  public NodeWrapper(CSVRecord record) {
    this.csvRecord = record;
    this.jsonNode = null;
  }

  public NodeWrapper() {
    this.csvRecord = null;
    this.jsonNode = null;
  }

  public Optional<CSVRecord> getCsvRecord() {
    return Optional.ofNullable(this.csvRecord);
  }

  public Optional<JsonNode> getJsonNode() {
    return Optional.ofNullable(this.jsonNode);
  }

  public void setCsvRecord(CSVRecord value) {
    if (value == null) throw new NoSuchElementException("Cannot set null values to CsvRecords");
    this.csvRecord = value;
  }

  public void setJsonNode(JsonNode value){
    if(value == null) throw new NoSuchElementException("Cannot set null values to JsonNodes");
    this.jsonNode = value;
  }
}
