package getcommand;

import common.FilterCommonOptions;
import common.SearchCommonOption;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

    @Inject
    GetResourceService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCommand.class);

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
            LOGGER.error("bad request `{}`", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("id does not exist `{}`", e);
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
            LOGGER.info("get USER : `{}`", result);
        } else {
            result = service.getUsers().toString();
            LOGGER.info("get USER(S) : `{}`", result);
        }
    }

    private void handleResource(String filter) {
        String result;
        if (options.id != null) {
            result = service.getUserWithId(options.id).toString();
            LOGGER.info("get USER : `{}`", result);
        } else if (filter != null && !filter.isBlank()) {
            result = service.getUserWithName(filter).toString();
            LOGGER.info("get filtered USER(S) : `{}`", result);
        } else {
            result = service.getUsers().toString();
            LOGGER.info("get USER(S) : `{}`", result);
        }
    }

}