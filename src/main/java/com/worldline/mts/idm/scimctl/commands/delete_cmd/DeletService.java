package com.worldline.mts.idm.scimctl.commands.delete_cmd;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.response.ErrorResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@Named("deleteService")
@Unremovable
public class DeletService {

  @Inject
  ClientConfig config;

  @Inject
  RequestUtils requestUtils;

  public <T extends ResourceNode> void deletUser(String id, Class<T> clazz) {
    requestUtils.deleteResource(id, clazz);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(DeletService.class);



}
