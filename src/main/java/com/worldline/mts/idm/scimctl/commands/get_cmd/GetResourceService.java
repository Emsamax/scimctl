package com.worldline.mts.idm.scimctl.commands.get_cmd;

import com.worldline.mts.idm.scimctl.utils.RequestUtils;

import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named("GetResourceService")
@Unremovable
public class GetResourceService {

  @Inject
  RequestUtils requestUtils;

  public <T extends ResourceNode> void getWithId(String id, Class<T> clazz) {
    requestUtils.getResource(id, clazz);
  }

  public <T extends ResourceNode> void getResources(Class<T> clazz) {
    requestUtils.getResources(clazz);
  }

  public <T extends ResourceNode> void getWithName(String name, Class<T> clazz) {
    requestUtils.getFilteredResources(clazz, name);
  }
}
