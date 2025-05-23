package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.worldline.mts.idm.scimctl.common.FilterCommonOptions;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


@Named("importService")
@Unremovable
@ApplicationScoped
public class ImportService {
  //TO NOT DO : manages duplicate user pas à moi de le faire

  @Inject
  ResourceStreamBuilder streamBuilder;


  @Inject
  RequestUtils requestUtils;

  /**
   * <p> and send a create request to the scim server </p>
   *
   * @param path to csv file
   * @throws RuntimeException if error isn't specified in norm RFC7644
   * @throws IOException      if error while reading the file
   */
  public void importResource(String path, FilterCommonOptions.ResourceType type) throws RuntimeException, IOException {
    // 1. convert file to stream of ressource
    // 2. Map the stream resource
    // for each item convert to User object
    // add Meta data
    // validate mandatory fields : userName, ...  -> wrappers/splitIterators sur stream<T extends ResourceNode>
    // validate other fields (email)
    // stream.map(this::toUserResource).filter().forEach(this::postResource)
    var creator = resolveResourceCreator(type);

    streamBuilder
      .fromFile(new File(path))
      .build()
      .convert()
      .chunk(50).forEach(creator);
  }

  private Consumer<List<JsonNode>> resolveResourceCreator(FilterCommonOptions.ResourceType type) {
    return switch (type) {
      case FilterCommonOptions.ResourceType.USER -> (node) -> requestUtils.createResources(node, User.class);
      case FilterCommonOptions.ResourceType.GROUP -> (node) -> requestUtils.createResources(node, Group.class);
    };
  }
}

