package com.worldline.mts.idm.scimctl.commands.delete_cmd;


import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.SearchCommonOption;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import static org.jboss.logging.Logger.getLogger;


@CommandLine.Command(name = "delete")
public class DeleteCommand implements Runnable {
  @CommandLine.Mixin
  SearchCommonOption options;

  @CommandLine.Mixin
  CommonOptions commonOptions;

  @Inject
  DeletService service;

  @Inject 
  OutputUtils utils;

  private static final Logger LOGGER = getLogger(DeleteCommand.class);

  @Override
  public void run() {
    try {
      handleRequest();
    } catch (BadRequestException e) {
      LOGGER.log(Logger.Level.valueOf("INFO"), "bad request "+ e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "id does not exist"+e.getMessage());
    }
  }

  private void handleRequest() {
    switch (commonOptions.resourceType) {
      case USER -> {
        if (options.id == null) {
          throw new BadRequestException("no resource specified");
        }
        utils.logMsg(LOGGER, Logger.Level.INFO, "Delete user with id "+ options.id);
        service.deletUser(options.id, User.class);
      }
      case GROUP -> {
        if (options.id == null) {
          throw new BadRequestException("no resource specified");
        }
        utils.logMsg(LOGGER, Logger.Level.INFO, "Delete group with id "+ options.id);
        service.deletUser(options.id, Group.class);
      }
    }
  }
}
