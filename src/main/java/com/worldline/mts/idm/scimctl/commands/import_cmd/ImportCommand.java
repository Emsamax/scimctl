package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.worldline.mts.idm.scimctl.common.CommonOptions;
import com.worldline.mts.idm.scimctl.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.common.IOCommonOptions;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import picocli.CommandLine;
import com.worldline.mts.idm.scimctl.common.ResourceTypeConverter;

import java.io.IOException;
import org.jboss.logging.Logger;

import static com.worldline.mts.idm.scimctl.common.FilterCommonOptions.ResourceType.GROUP;
import static com.worldline.mts.idm.scimctl.common.FilterCommonOptions.ResourceType.USER;

@CommandLine.Command(name = "import")
public class ImportCommand implements Runnable {
  @Inject
  ImportService service;

  @Inject
  Logger LOGGER;

  @Inject
  CommonOptions options;


  /**
   * Force the user to specify either the path to the JSON file or write the JSON data directly into the console.
   */
  @CommandLine.ArgGroup(heading = "Resource creation options:%n", multiplicity = "1")
  IOCommonOptions ioOptions;

  @Override
  public void run() {
    try {
      switch (options.resourceType) {
        case USER -> {
          LOGGER.log(org.jboss.logging.Logger.Level.valueOf("INFO"), "import USER from file path : `{}`"+ ioOptions.path);
          service.importResource(ioOptions.path, USER);
        }

        case GROUP -> {
          LOGGER.log(org.jboss.logging.Logger.Level.valueOf("INFO"), "import GROUP from file path : `{}`"+ ioOptions.path);
          service.importResource(ioOptions.path, GROUP);
        }
      }
    } catch (JsonProcessingException e) {
      LOGGER.log(org.jboss.logging.Logger.Level.valueOf("ERROR"),"error parsing data `{}`"+ e.getMessage());
    } catch (BadRequestException e) {
      LOGGER.log(org.jboss.logging.Logger.Level.valueOf("ERROR"),"bad request : `{}`"+ e.getMessage());
    } catch (RuntimeException e) {
      LOGGER.log(org.jboss.logging.Logger.Level.valueOf("ERROR"),"error isn't describe in RFC7644 : `{}`"+ e.getMessage());
    } catch (IOException e) {
      LOGGER.log(org.jboss.logging.Logger.Level.valueOf("ERROR"),"error reading the file : `{}`"+ e.getMessage());
    }
  }
}
