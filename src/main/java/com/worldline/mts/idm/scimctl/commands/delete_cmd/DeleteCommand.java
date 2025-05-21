package com.worldline.mts.idm.scimctl.commands.delete_cmd;

import com.worldline.mts.idm.scimctl.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.common.ResourceTypeConverter;
import com.worldline.mts.idm.scimctl.common.SearchCommonOption;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "delete")
public class DeleteCommand implements Runnable {
  @CommandLine.ArgGroup(exclusive = false)
  SearchCommonOption options;

  @Inject
  DeletService service;

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCommand.class);

  @Override
  public void run() {
    try {
      handleRequest();
    } catch (BadRequestException e) {
      LOGGER.error("bad request `{}`", e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.error("id does not exist `{}`", e.getMessage());
    }
  }

  private void handleRequest() {
    switch (options.resourceType) {
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
