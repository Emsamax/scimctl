package com.worldline.mts.idm.scimctl.commands.update_cmd;

import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;

import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;

import java.io.File;
import java.io.IOException;

@ApplicationScoped
@Named("UpdateService")
@Unremovable
public class UpdateService {

  @Inject
  RequestUtils requestUtils;

  @Inject
  ScimCtlBeansConfig config;

  public <T extends ResourceNode> void updateUserFromFile(String id, String path, Class<T> clazz)
      throws IOException, BadRequestException {
    var foramter = config.getNodeFormater();
    config.getResourceStreamBuilder(foramter)
        .fromFile(new File(path))
        .build()
        .convert()
        .chunk(config.getBatchSize())
        .forEach(nodeWrapperList -> {
          requestUtils.updateResource(id, clazz, nodeWrapperList.getFirst().getJsonNode().get());
        });
  }

  public <T extends ResourceNode> void updateUserFromText(String id, String resource, Class<T> clazz)
      throws IOException, BadRequestException {
    requestUtils.updateResource(id, clazz, config.getObjectMapper().readTree(resource));
  }

}
