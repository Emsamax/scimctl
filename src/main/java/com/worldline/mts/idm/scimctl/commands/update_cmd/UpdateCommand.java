package com.worldline.mts.idm.scimctl.commands.update_cmd;

import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.IOCommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.SearchCommonOption;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import picocli.CommandLine;
import org.jboss.logging.Logger;
import java.io.IOException;

@ApplicationScoped
@Named("upDateCommand")
@CommandLine.Command(name = "update")
public class UpdateCommand implements Runnable {

  @Inject
  Logger LOGGER;
  @Inject
  UpdateService service;

  @Inject
  CommonOptions common;
  /**
   * force the user to specify both the id and user.json .
   */
  @CommandLine.ArgGroup(heading = "Update options:%n", exclusive = false, multiplicity = "1")
  SearchCommonOption search;

  @CommandLine.ArgGroup(heading = "IO options:%n", exclusive = false, multiplicity = "1")
  IOCommonOptions ioOptions;

  @Inject
  OutputUtils utils;

  @Override
  public void run() {
    try {
      switch (common.resourceType) {
        case USER -> {
          if (search.id != null && ioOptions.path != null) {
            utils.logMsg(LOGGER, Logger.Level.INFO, "update user");
            service.updateUser(search.id, ioOptions.text, User.class);
          }
        }
        case GROUP -> {
          LOGGER.info("update group not done yet");
        }
      }
    } catch (IOException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "error reading the file : " + e.getMessage());
    }
  }
}
