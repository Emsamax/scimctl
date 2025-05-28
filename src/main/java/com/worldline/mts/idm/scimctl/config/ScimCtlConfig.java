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

  @Inject
  ObjectMapper mapper;

  public ObjectMapper getObjectMapper(){
    return mapper;
  }

  @Produces
  public NodeFormater getNodeFormater(){
    return new NodeFormater(mapper);
  }

  @Produces
  public CsvMapper getCsvMapper(){
    return new CsvMapper();
  }

  @Produces
  public ResourceStreamBuilder getResourceStreamBuilder(){
    return new ResourceStreamBuilder(getCsvMapper(), getNodeFormater());
  }
}
