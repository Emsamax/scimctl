package com.worldline.mts.idm.scimctl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.NodeFormater;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class ScimCtlConfig {

  @Produces
  public ObjectMapper getObjectMapper(){
    return mapper;
  }

  @ApplicationScoped
  @Produces
  public NodeFormater getNodeFormater(ObjectMapper mapper){
    return new NodeFormater(mapper);
  }

  @ApplicationScoped
  @Produces
  public CsvMapper getCsvMapper(){
    return new CsvMapper();
  }

  @ApplicationScoped
  @Produces
  public ResourceStreamBuilder getResourceStreamBuilder(CsvMapper csvMapper, NodeFormater formater){
    return new ResourceStreamBuilder(csvMapper, formater);
  }
}
