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

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;

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

  private static final String ERR_MSG = "must specified an ID, ex : 393ab372-b5d8-478c-ac4c-6c100b5a66bc";

  @Override
  public void run() {
    try {
      handleRequest();
    } catch (BadRequestException e) {
      System.err.println();
    } catch (IllegalArgumentException e) {
      System.err.println("id does not exist" + e.getMessage());
    }
  }

  private void handleRequest() {
    switch (commonOptions.resourceType) {
      case USER -> {
        if (options.id == null) {
          System.err.println(ERR_MSG);
        } else {
          service.deletUser(options.id, User.class);
          System.out.printf("user %s deleted", options.id);
        }

      }
      case GROUP -> {
        if (options.id == null) {
          System.err.println(ERR_MSG);
        } else {
          System.out.printf("group %s deleted" + options.id);
          service.deletUser(options.id, Group.class);
        }
      }
    }
  }
}
