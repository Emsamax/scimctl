package com.worldline.mts.idm.scimctl.commands.schema_cmd;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;


@ApplicationScoped
@Named("schemaService")
@Unremovable
public class SchemaService {

  @Inject
  ClientConfig config;

  @Inject
  RequestUtils utils;

  public void getSchema(){
    utils.getSchemas();
  }
}
