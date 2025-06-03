package com.worldline.mts.idm.scimctl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class ScimCtlConfig {
  @Inject
  ObjectMapper mapper;


  public ObjectMapper getObjectMapper() {
    return this.mapper;
  }

  @ApplicationScoped
  @Produces
  public NodeFormater getNodeFormater() {
    return new NodeFormater(this.mapper);
  }


  @ApplicationScoped
  @Produces
  public CsvMapper getCsvMapper() {
    return new CsvMapper();
  }

  @ApplicationScoped
  @Produces
  public ResourceStreamBuilder getResourceStreamBuilder(CsvMapper csvMapper, NodeFormater formater) {
    return new ResourceStreamBuilder(csvMapper, formater);
  }
}
