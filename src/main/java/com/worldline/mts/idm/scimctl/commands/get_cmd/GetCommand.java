package com.worldline.mts.idm.scimctl.commands.get_cmd;

import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.SearchCommonOption;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.base.ScimObjectNode;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import static org.jboss.logging.Logger.getLogger;

import org.jboss.logging.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

  @Inject
  GetResourceService service;

  private static final Logger LOGGER = getLogger(GetCommand.class);

  /**
   * Force the user to specify either the id or the username of the user to get.
   */
  @CommandLine.Mixin
  SearchCommonOption search;

  @Inject
  OutputUtils utils;

  @CommandLine.ArgGroup(heading = "User search options:%n", exclusive = false)
  FilterCommonOptions filter;

  @CommandLine.Mixin
  CommonOptions options;

  @Override
  public void run() {
    handleRequest();
  }

  public void handleRequest() {
    if (filter == null && search == null) {
      handleResource();
    } else if (filter != null) {
      getByName(filter.userName);
    } else if (search != null) {
      getById(search.id);
    }
  }

  public void handleResource() {
    utils.logMsg(LOGGER, Logger.Level.INFO, "Get user");
    service.getUsers();
  }

  private void getById(String id) {
    service.getUserWithId(id);
  }

  private void getByName(String name) {
    service.getUserWithName(name);
  }
}