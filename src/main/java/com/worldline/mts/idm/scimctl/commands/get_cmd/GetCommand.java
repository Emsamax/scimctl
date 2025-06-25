package com.worldline.mts.idm.scimctl.commands.get_cmd;
import com.worldline.mts.idm.scimctl.options.CommonOptions;
import com.worldline.mts.idm.scimctl.options.FilterOptions;
import com.worldline.mts.idm.scimctl.options.SearchOptions;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;

import picocli.CommandLine;

@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

  @Inject
  GetResourceService service;

  /**
   * Force the user to specify either the id or the username of the user to get.
   */
  @CommandLine.Mixin
  SearchOptions search;

  @Inject
  OutputUtils utils;

  @CommandLine.Mixin
  FilterOptions filter;

  @CommandLine.Mixin
  CommonOptions options;

  @Override
  public void run() {
    if (options.resourceType == null) {
      System.err.println("you must percise a resource type ");
      return;
    }
    if (filter.userName == null && search.id == null) {
      handleResource();
      return;
    }
    if (filter.userName != null) {
      getByName(filter.userName);
      return;
    }
    if (search.id != null) {
      getById(search.id);
      return;
    }
  }

  public void handleResource() {
    utils.logMsg("Get resource");
    service.getResources(resolveResourceType());
  }

  private void getById(String id) {
    service.getWithId(id, resolveResourceType());
  }

  private void getByName(String name) {
    service.getWithName(name, resolveResourceType());
  }

  @SuppressWarnings("unchecked")
  private <T extends ResourceNode> Class<T> resolveResourceType() {
    switch (options.resourceType) {
      case USER:
        return (Class<T>) User.class;
      case GROUP:
        return (Class<T>) Group.class;
    }
    return null;
  }
}