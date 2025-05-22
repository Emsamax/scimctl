package com.worldline.mts.idm.scimctl.commands.get_cmd;

import com.worldline.mts.idm.scimctl.common.FilterCommonOptions;
import com.worldline.mts.idm.scimctl.common.SearchCommonOption;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.jboss.logging.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

    @Inject
    GetResourceService service;

    @Inject
    Logger LOGGER;

    /**
     * Force the user to specify either the id or the username of the user to get.
     */
    @CommandLine.ArgGroup(heading = "User search options:%n", exclusive = false, multiplicity = "1")
    SearchCommonOption options;

    @CommandLine.ArgGroup(heading = "User search options:%n", exclusive = false)
    FilterCommonOptions filter;


    @Override
    public void run() {
        try {
            handleRequest();
        } catch (BadRequestException e) {
          LOGGER.log(Logger.Level.valueOf("ERROR"),"bad request :"+ e.getMessage());
        } catch (IllegalArgumentException e) {
          LOGGER.log(Logger.Level.valueOf("ERROR"),"id does not exist "+ e.getMessage());
        }
    }

    public void handleRequest() {
        if (filter == null) {
            handleResource();
        } else {
           handleResource(filter.userName);
        }
    }

    public void handleResource() {
        String result;
        if (options.id != null) {
            result = service.getUserWithId(options.id).toString();
          LOGGER.log(Logger.Level.valueOf("INFO"),"get USER : "+ result);
        } else {
          LOGGER.info("get USER(S) :");
          service.getUsers().forEach(u -> LOGGER.log(Logger.Level.valueOf("INFO"), u.toPrettyString()));
        }
    }

    private void handleResource(String filter) {
        String result;
        if (options.id != null) {
            result = service.getUserWithId(options.id).toString();
          LOGGER.log(Logger.Level.valueOf("INFO"),"get USER : "+ result);
        } else if (filter != null && !filter.isBlank()) {
            result = service.getUserWithName(filter).toString();
          LOGGER.log(Logger.Level.valueOf("INFO"),"get filtered USER(S) : "+ result);
        } else {
            result = service.getUsers().toString();
          LOGGER.log(Logger.Level.valueOf("ERROR"),"get USER(S) : `{}`"+ result);
        }
    }

}