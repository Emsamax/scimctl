package com.worldline.mts.idm.scimctl.commands.get_cmd;

import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.SearchCommonOption;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

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
    try {
      handleRequest();
    } catch (BadRequestException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "bad request :" + e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "id does not exist " + e.getMessage());
    }
  }

  public void handleRequest() {
    if (filter == null) {
      handleResource();
    } else {
      handleResource(filter.userName);
    }
  }

  public void handleResource() {
    String result;
    if (search.id != null) {
      result = service.getUserWithId(search.id).toString();
      utils.logMsg(LOGGER, Logger.Level.INFO, "Get user with id : " + result);
    } else {
      utils.logMsg(LOGGER, Logger.Level.INFO, "Get user");
      service.getUsers().forEach(u -> LOGGER.log(Logger.Level.valueOf("INFO"), u.toPrettyString()));
    }
  }

  private void handleResource(String filter) {
    String result;
    if (search.id != null) {
      result = service.getUserWithId(search.id).toString();
      utils.logMsg(LOGGER, Logger.Level.INFO, result);
    } else if (filter != null && !filter.isBlank()) {
      result = service.getUserWithName(filter).toString();
      utils.logMsg(LOGGER, Logger.Level.INFO, "user :" + result);
    } else {
      result = service.getUsers().toString();
      utils.logMsg(LOGGER, Logger.Level.INFO, "users :" + result);
    }
  }

}