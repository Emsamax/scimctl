package GetRessource;

import createCommand.ResourceType;
import createCommand.ResourceTypeConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;


@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

    @Inject
    GetResourceService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCommand.class);
    @Named("resourceService")
    @Inject
    GetResourceService getResourceService;

    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type",
            converter = ResourceTypeConverter.class,
            required = true)
    private ResourceType resourceType;

    /**
     * Force the user to specify either the id or the username of the user to get.
     */
    @CommandLine.ArgGroup(heading = "User search options:%n")
    GetExclusiveOptions getExclusiveOptions;

    @Override
    public void run() {
        try {
            if (resourceType.equals(ResourceType.USER) && getExclusiveOptions != null) {
                if (getExclusiveOptions.id != null) {
                    LOGGER.info("get USER : `{}`", service.getUserWithId(getExclusiveOptions.id));
                } else if (getExclusiveOptions.userName != null) {
                    LOGGER.info("get filtered USER(S) : `{}`", service.getUserWithName(getExclusiveOptions.userName));
                } else {
                    LOGGER.info("get USER(S) : `{}`", service.getUsers());
                }
            } else if (resourceType.equals(ResourceType.GROUP) && getExclusiveOptions != null) {
                LOGGER.info("get GROUP : `{}`", service.getGroupWithName(getExclusiveOptions.userName));
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