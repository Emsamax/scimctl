package com.worldline.mts.idm.scimctl.commands.update_cmd;

import com.worldline.mts.idm.scimctl.common.IOCommonOptions;
import com.worldline.mts.idm.scimctl.common.SearchCommonOption;
import jakarta.inject.Inject;
import picocli.CommandLine;
import org.jboss.logging.Logger;
import java.io.IOException;

@CommandLine.Command(name = "update")
public class UpdateCommand implements Runnable {

  @Inject
  Logger LOGGER;
  @Inject
  UpdateService service;
  /**
   * force the user to specify both the id and user.json .
   */
  @CommandLine.ArgGroup(heading = "Update options:%n", exclusive = false, multiplicity = "1")
  SearchCommonOption options;

  @CommandLine.ArgGroup(heading = "IO options:%n", exclusive = false, multiplicity = "1")
  IOCommonOptions Options;


  @Override
  public void run() {
    try {
      switch (options.resourceType) {
        case USER -> {
          if (options.id != null && Options.path != null) {
            service.updateUser(options.id, Options.path);
          }
        }
      }
    } catch (IOException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "error reading the file : "+ e.getMessage());
    }


  }
}
