package com.worldline.mts.idm.scimctl.commands.delete_cmd;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named("deleteService")
@Unremovable
public class DeletService {

  @Inject
  ClientConfig config;

  @Inject
  RequestUtils requestUtils;

  public <T extends ResourceNode> void deleteResource(String id, Class<T> clazz) {
    requestUtils.deleteResource(id, clazz);
  }
}
