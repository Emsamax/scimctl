package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jboss.logging.Logger;


import java.io.IOException;

//TO NOT DO : manages duplicate user pas à moi de le faire

@ApplicationScoped
public class CreateService {

  @Inject
  ObjectMapper mapper;

  @Inject
  Logger LOGGER;

  @Named("requestUtils")
  @Inject
  RequestUtils requestUtils;

  /**
   * @param data  contains the data you want to create. The data must be in JSON format.
   * @param clazz is the Object.Class you want to create from the data. User.class or Group.Class for example.
   */
  public <T extends ResourceNode> void createResource(String data, Class<T> clazz) throws IOException, IllegalArgumentException {
    LOGGER.infof("input data : %s", data);
    //String reformattedData = reformate(data);
    var myMapper = new ObjectMapper();
    data = data.replaceAll("\\\\", "");
    LOGGER.infof("cleaned data : %s", data);

    var resource = myMapper.readValue(data, JsonNode.class);
    LOGGER.info("JSON parsed : " + resource.toString());
    LOGGER.info("pretty : " + resource.toPrettyString());
    requestUtils.createResource(resource, clazz);
  }
}
