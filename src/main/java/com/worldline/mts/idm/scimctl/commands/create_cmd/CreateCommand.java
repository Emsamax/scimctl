package com.worldline.mts.idm.scimctl.commands.create_cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.worldline.mts.idm.scimctl.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.common.IOCommonOptions;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import com.worldline.mts.idm.scimctl.common.ResourceTypeConverter;

import java.io.IOException;

//TODO : plain text
//TODO : check file extension if not json throw exception
@CommandLine.Command(name = "create")
public class CreateCommand implements Runnable {
    @Inject
    CreateService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCommand.class);

    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type (USER or GROUP)",
            converter = ResourceTypeConverter.class,
            required = true)
    private FilterCommonOptions.ResourceType resourceType;

    /**
     * Force the user to specify either the path to the JSON file or write the JSON data directly into the console.
     */
    @CommandLine.ArgGroup(heading = "Resource creation options:%n", multiplicity = "1")
    IOCommonOptions options;

    //TODO : pourquoi faire ça si pas dans les specs ?
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
                    LOGGER.info("create USER from file path : `{}`", options.text);
                    service.createResource(options.text, User.class);
                }
                case GROUP -> {
                    LOGGER.info("create GROUP from file path : `{}`", options.text);
                    service.createResource(options.text, Group.class);
                }
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("error parsing JSON data `{}`", e.getMessage());
            System.err.println("error parsing JSON data :" + e.getMessage());
        } catch (BadRequestException e) {
            LOGGER.error("bad request : `{}`", e.getMessage());
            System.err.println("error :" + e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.error("error isn't describe in RFC7644 : `{}`", e.getMessage());
            System.err.println("error isn't describe in RFC7644 :" + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("error reading the file : `{}`", e.getMessage());
            System.err.println("error reading the file : " + e.getMessage());
        }
    }


}
