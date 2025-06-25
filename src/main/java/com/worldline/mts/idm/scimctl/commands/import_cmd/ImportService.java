package com.worldline.mts.idm.scimctl.commands.import_cmd;
import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;
import com.worldline.mts.idm.scimctl.options.ResourceType;

import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeWrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Named("importService")
@Unremovable
@ApplicationScoped
public class ImportService {

  @Inject
  ScimCtlBeansConfig config;

  @Inject
  RequestUtils requestUtils;

  /**
   * <p>
   * and send a create request to the scim server
   * </p>
   *
   * @param path to csv file
   * @throws RuntimeException if error isn't specified in norm RFC7644
   * @throws IOException      if error while reading the file
   */
  public void importResource(String path, ResourceType type) throws RuntimeException, IOException {
    // 1. convert file to stream of ressource
    // 2. Map the stream resource
    // for each item convert to User object
    // add Meta data
    // validate mandatory fields : userName, ... -> wrappers/splitIterators sur
    // stream<T extends ResourceNode>
    // validate other fields (email)
    // stream.map(this::toUserResource).filter().forEach(this::postResource)
    var creator = resolveResourceCreator(type);

    var foramter = config.getNodeFormater();
    config.getResourceStreamBuilder(foramter)
        .fromFile(new File(path))
        .build()
        .convert()
        .chunk(config.getBatchSize())
        .forEach(creator);
  }

  private Consumer<List<NodeWrapper>> resolveResourceCreator(ResourceType type) {
    return switch (type) {
      case USER -> (node) -> requestUtils.createResources(node, User.class);
      case GROUP -> (node) -> requestUtils.createResources(node, Group.class);
    };
  }
}
