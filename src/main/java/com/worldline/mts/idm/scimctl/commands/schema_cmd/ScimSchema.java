package com.worldline.mts.idm.scimctl.commands.schema_cmd;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import picocli.CommandLine;

import org.jboss.logging.Logger;

@ApplicationScoped
@CommandLine.Command(name = "schema")
public class ScimSchema implements Runnable {

  @Inject
  Logger LOGGER;

  @Inject
  SchemaService schemaService;

  @Override
  public void run() {
    try {
      LOGGER.log(Logger.Level.valueOf("INFO"), "get SCHEMA : " + schemaService.getSchema());
    } catch (BadRequestException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "bad request : " + e.getMessage());
    }

  }
}
