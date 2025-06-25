package com.worldline.mts.idm.scimctl.commands.update_cmd;

import com.worldline.mts.idm.scimctl.options.CommonOptions;
import com.worldline.mts.idm.scimctl.options.IOOptions;
import com.worldline.mts.idm.scimctl.options.SearchOptions;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(name = "update")
public class UpdateCommand implements Runnable {

  @Inject
  UpdateService service;

  @CommandLine.Mixin
  CommonOptions options;
  /**
   * force the user to specify both the id and user.json .
   */
  @CommandLine.ArgGroup(heading = "Update options:%n", exclusive = true)
  SearchOptions search;

  @CommandLine.ArgGroup(heading = "IO options:%n", exclusive = true)
  IOOptions ioOptions;

  @Inject
  OutputUtils utils;

  @Override
  public void run() {
    if (options.resourceType == null) {
      System.err.println("you must percise a resource type ");
      return;
    }
    switch (options.resourceType) {
      case USER -> handleArgs(search, ioOptions, User.class);
      case GROUP -> handleArgs(search, ioOptions, Group.class);
    }
  }

  private <T extends ResourceNode> void handleArgs(SearchOptions search, IOOptions iOoptions,
      Class<T> clazz) {
    if (search == null) {
      System.err.println("must precise an id");
      return;
    }
    if (ioOptions == null) {
      System.err.println("must precise a file path or content");
      return;
    }
    try {
      if (ioOptions.path != null) {
        service.updateUserFromFile(search.id, ioOptions.path, clazz);
      }
      
      if (ioOptions.text != null) {
        service.updateUserFromText(search.id, ioOptions.text, clazz);
      }
    } catch (IOException e) {
      System.err.println("error reading the file : " + e.getMessage());
    }
  }

}
