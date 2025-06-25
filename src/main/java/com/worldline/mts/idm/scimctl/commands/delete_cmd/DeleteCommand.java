package com.worldline.mts.idm.scimctl.commands.delete_cmd;

import com.worldline.mts.idm.scimctl.options.CommonOptions;
import com.worldline.mts.idm.scimctl.options.SearchOptions;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "delete")
public class DeleteCommand implements Runnable {
  @CommandLine.Mixin
  SearchOptions options;

  @CommandLine.Mixin
  CommonOptions common;

  @Inject
  DeletService service;

  @Inject
  OutputUtils utils;

  private static final String ERR_MSG = "must specified an ID, ex : 393ab372-b5d8-478c-ac4c-6c100b5a66bc";

  @Override
  public void run() {
    if (common.resourceType == null) {
      System.err.println("you must percise a resource type");
      return;
    }
    if (options.id == null) {
      System.err.println(ERR_MSG);
      return;
    }
    switch (common.resourceType) {
      case USER -> {
        service.deleteResource(options.id, User.class);
      }
      case GROUP -> {
        service.deleteResource(options.id, Group.class);
      }
    }
  }
}
