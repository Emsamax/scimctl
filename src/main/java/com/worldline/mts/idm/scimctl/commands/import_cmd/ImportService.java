package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.worldline.mts.idm.scimctl.config.ClientConfig;
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

import static java.lang.System.*;


@Named("importService")
@Unremovable
@ApplicationScoped
public class ImportService {
  //TO NOT DO : manages duplicate user pas à moi de le faire

  @Inject
  ResourceStreamBuilder streamBuilder;

  @Inject
  ResourceStreamConverter streamConverter;

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

    /*Class clazz = User.class;
    if (type == FilterCommonOptions.ResourceType.GROUP) {
      clazz = Group.class;
    }*/
    if (type == FilterCommonOptions.ResourceType.USER) {
      System.out.println(" ============== TEST ================");
      // requestUtils.createResourcesRequest(streamBuilder.build(new File(path), User.class), User.class);
      //streamBuilder.build(new File(path))
            //  .map(jsonNode ->jsonNode.get("userName").toString()).toList();

      var stream = streamBuilder.build(new File(path));
      var converted = streamConverter.convert(stream, User.class);
      var chunks = streamConverter.chunk(converted, 50).toList();
      for(List<JsonNode> chunk: chunks ){
        requestUtils.createResourcesRequest(chunk, User.class);
      }
      //var customSpliterator = new CustomSpliterator<>(converted.toList());
      //var splitNodes = customSpliterator.trySplit();
      //out.println(splitNodes);

      //var customSpliterator = new CustomSpliterator<JsonNode>(converted.toList());

    } else if (type == FilterCommonOptions.ResourceType.GROUP) {
      //requestUtils.createResourcesRequest(streamBuilder.build(new File(path), Group.class), Group.class);
    }
  }
}

