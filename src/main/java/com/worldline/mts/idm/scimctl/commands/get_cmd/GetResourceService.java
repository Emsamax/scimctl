package com.worldline.mts.idm.scimctl.commands.get_cmd;

import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
@Named("GetResourceService")
@Unremovable
public class GetResourceService {

  @Inject
  RequestUtils requestUtils;

  public User getUserWithId(String id) throws BadRequestException, IllegalArgumentException {
    return requestUtils.getResource(id, User.class);
  }

  public List<User> getUsers() throws BadRequestException {
    return requestUtils.getResources(User.class);
  }

  public List<User> getUserWithName(String name) throws BadRequestException {
    return requestUtils.getFilteredResources(User.class, name);
  }
}
