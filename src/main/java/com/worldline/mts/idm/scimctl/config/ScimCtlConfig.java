package com.worldline.mts.idm.scimctl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.NodeFormater;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import javax.print.attribute.standard.JobKOctets;

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
