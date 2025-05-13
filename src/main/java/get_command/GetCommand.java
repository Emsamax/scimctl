package get_command;

import common.SearchCommonOption;
import resource_type.ResourceType;
import resource_type.ResourceTypeConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

//TODO : see service
@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

    @Inject
    GetResourceService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCommand.class);
    @Named("resourceService")
    @Inject
    GetResourceService getResourceService;

    /**
     * Force the user to specify either the id or the username of the user to get.
     */
    @CommandLine.ArgGroup(heading = "User search options:%n", exclusive = false, multiplicity = "1")
    SearchCommonOption options;

    @Override
    public void run() {
        try {
            if (options.resourceType.equals(ResourceType.USER)) {
                if (options.id != null) {
                    LOGGER.info("get USER : `{}`", service.getUserWithId(options.id));
                } else if (options.userName != null) {
                    LOGGER.info("get filtered USER(S) : `{}`", service.getUserWithName(options.userName));
                } else {
                    LOGGER.info("get USER(S) : `{}`", service.getUsers());
                }
            } else if (options.equals(ResourceType.GROUP)) {
                LOGGER.info("get GROUP : `{}`", service.getGroupWithName(options.userName));
            } else {
                LOGGER.info("get GROUP(S) :` {}` ", service.getGroups());
            }
        } catch (BadRequestException e) {
            LOGGER.error("bad request : `{}`", e.getMessage());
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("id does not exist : `{}`", e.getMessage());
            System.err.println(e.getMessage());
        }
    }

}