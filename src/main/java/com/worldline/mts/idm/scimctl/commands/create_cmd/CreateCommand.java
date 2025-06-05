package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.worldline.mts.idm.scimctl.commands.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.IOCommonOptions;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import picocli.CommandLine;
import com.worldline.mts.idm.scimctl.commands.common.ResourceTypeConverter;
import java.io.IOException;

import org.jboss.logging.Logger;

@CommandLine.Command(name = "create")
public class CreateCommand implements Runnable {
  @Inject
  CreateService service;

  @Inject
  Logger LOGGER;


  @CommandLine.Option(names = {"--resource-type", "-t"},
    description = "Resource type (USER or GROUP)",
    converter = ResourceTypeConverter.class,
    required = true)
  private FilterCommonOptions.ResourceType resourceType;

  /**
   * Force the user to specify either the path to the JSON file or write the JSON data directly into the console.
   */
  @CommandLine.ArgGroup(heading = "Resource creation options:%n", multiplicity = "1")
  IOCommonOptions ioOptions;
    /*
    private ResourceNode resolveReourceNodeFromName(String name) {
        return switch (name) {
            case "user" -> new User();
            case "group"-> new de.captaingoldfish.scim.sdk.common.resources.Group();
            default -> throw new RuntimeException("resource type not found");
        };
    }*/

  @Override
  public void run() {
      /*
        var name = "user";
        var mapper = JsonMapper.builder().build();
        try {
            var resource = mapper.readValue("data parm", resolveReourceNodeFromName(name).getClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
    try {
      switch (resourceType) {
        case USER -> {
          LOGGER.log(Logger.Level.valueOf("INFO"), "create USER from text : " + ioOptions.text);
          service.createResource(ioOptions.text, User.class);
        }
        case GROUP -> {
          LOGGER.log(Logger.Level.valueOf("INFO"), "create GROUP from text " + ioOptions.text);
          service.createResource(ioOptions.text, Group.class);
        }
      }
    } catch (JsonProcessingException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "error parsing JSON data : " + e.getMessage());
    } catch (BadRequestException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"),"bad request : "+e.getMessage());
    } catch (RuntimeException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"),"error isn't describe in RFC7644 : "+ e.getMessage());
    } catch (IOException e) {
      LOGGER.log(Logger.Level.valueOf("ERROR"),"error reading the file : "+ e.getMessage());
    }
  }


}
