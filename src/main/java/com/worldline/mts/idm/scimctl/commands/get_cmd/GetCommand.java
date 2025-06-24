package com.worldline.mts.idm.scimctl.commands.get_cmd;

import static com.worldline.mts.idm.scimctl.commands.common.FilterCommonOptions.ResourceType.USER;

import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.SearchCommonOption;
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
  SearchCommonOption search;

  @Inject
  OutputUtils utils;

  @CommandLine.Mixin
  FilterCommonOptions filter;

  @CommandLine.Mixin
  CommonOptions options;

  @Override
  public void run() {
    handleRequest();
  }

  public void handleRequest() {
    if (options.resourceType == null) {
      System.err.println("you must percise a resource type ");
      return;
    }
    if (filter.userName == null && search.id == null) {
      handleResource();
    } else if (filter.userName != null) {
      getByName(filter.userName);
    } else if (search.id != null) {
      getById(search.id);
    }
  }

  public void handleResource() {
    utils.logMsg("Get user");
    service.getResources(resolveResourceType());
  }

  private void getById(String id) {
    service.getWithId(id, resolveResourceType());
  }

  private void getByName(String name) {
    service.getWithName(name, resolveResourceType());
  }

  private <T extends ResourceNode> Class<T> resolveResourceType(){
    switch (options.resourceType) {
      case USER:
        return (Class<T>) User.class;
      case GROUP:
        return (Class<T>) Group.class;
    }
    return null;
  } 
}