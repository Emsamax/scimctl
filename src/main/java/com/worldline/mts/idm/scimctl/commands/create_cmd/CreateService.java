package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;
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
  Logger LOGGER;

  RequestUtils requestUtils;

  @Inject
  ScimCtlBeansConfig config;

  /**
   * @param data  contains the data you want to create. The data must be in JSON
   *              format.
   * @param clazz is the Object.Class you want to create from the data. User.class
   *              or Group.Class for example.
   */
  public <T extends ResourceNode> void createResource(String data, Class<T> clazz) {
    try {
      data = data.replaceAll("\\\\", "");
      var resource = config.getObjectMapper().readValue(data, JsonNode.class);
      requestUtils.createResource(resource, clazz);
    } catch (JsonParseException e) {
      System.err.println("error while parsing the string : " + data);
    } catch (IOException e) {
      System.err.println("error while data");
    }
  }
}
