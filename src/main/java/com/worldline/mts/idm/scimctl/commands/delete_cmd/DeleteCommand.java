package com.worldline.mts.idm.scimctl.commands.delete_cmd;


import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.SearchCommonOption;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import static org.jboss.logging.Logger.getLogger;


@CommandLine.Command(name = "delete")
public class DeleteCommand implements Runnable {
  @CommandLine.ArgGroup(exclusive = false)
  SearchCommonOption options;

  @Inject
  CommonOptions commonOptions;

  @Inject
  DeletService service;

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
        if (options.id != null) {
          throw new BadRequestException("no resource specified");
        }
        service.deletUser(options.id, User.class);
      }
      case GROUP -> {
        if (options.id != null) {
          throw new BadRequestException("no resource specified");
        }
        service.deletUser(options.id, Group.class);
      }
    }
  }
}
