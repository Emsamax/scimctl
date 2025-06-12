package com.worldline.mts.idm.scimctl.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class ScimCtlBeansConfig {

  int batch_size;

  public void setBatchSize(int batch_size) {
    this.batch_size = batch_size;
  }

  public int getBatchSize() {
    return this.batch_size;
  }

  public ScimCtlBeansConfig(@ConfigProperty(name = "batch.size") int batch_size) {
    this.batch_size = batch_size;
  }

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
  public ResourceStreamBuilder getResourceStreamBuilder(NodeFormater formater) {
    return new ResourceStreamBuilder(formater);
  }

  @ApplicationScoped
  @Produces
  public ResourceStreamBuilder getResourceStreamBuilder() {
    return new ResourceStreamBuilder(getNodeFormater());
  }
}
