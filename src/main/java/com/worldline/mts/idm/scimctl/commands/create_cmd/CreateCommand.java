package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;

import com.worldline.mts.idm.scimctl.commands.common.IOCommonOptions;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import picocli.CommandLine;



import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import org.jboss.logging.Logger;

@CommandLine.Command(name = "create")
public class CreateCommand implements Runnable {
  @Inject
  CreateService service;

  @Inject
  Logger LOGGER;

  @Inject
  OutputUtils utils;

  @CommandLine.Mixin
  CommonOptions common;

  /**
   * Force the user to specify either the path to the JSON file or write the JSON
   * data directly into the console.
   */
  @CommandLine.ArgGroup(heading = "Resource creation options:%n", multiplicity = "1")
  IOCommonOptions ioOptions;
  /*
   * private ResourceNode resolveReourceNodeFromName(String name) {
   * return switch (name) {
   * case "user" -> new User();
   * case "group"-> new de.captaingoldfish.scim.sdk.common.resources.Group();
   * default -> throw new RuntimeException("resource type not found");
   * };
   * }
   */

  @Override
  public void run() {
    if (common.resourceType == null) {
      System.err.println("you must percise a resource type");
      return;
    }
    switch (common.resourceType) {
      case USER -> {
        utils.logMsg("create USER from text : " + ioOptions.text);
        service.createResource(ioOptions.text, User.class);
      }
      case GROUP -> {
        utils.logMsg("create GROUP from text " + ioOptions.text);
        service.createResource(ioOptions.text, Group.class);
      }
    }
  }

}
