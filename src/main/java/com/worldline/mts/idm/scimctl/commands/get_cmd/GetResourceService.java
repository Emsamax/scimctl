package com.worldline.mts.idm.scimctl.commands.get_cmd;

import com.worldline.mts.idm.scimctl.utils.RequestUtils;
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

  public void getUserWithId(String id) {
     requestUtils.getResource(id, User.class);
  }

  public void getUsers() {
     requestUtils.getResources(User.class);
  }

  public void getUserWithName(String name) {
     requestUtils.getFilteredResources(User.class, name);
  }
}
