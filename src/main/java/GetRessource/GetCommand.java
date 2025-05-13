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
    ExclusiveOptions exclusiveOptions;

    @Override
    public void run() {
        try {
            if (resourceType.equals(ResourceType.USER) && exclusiveOptions != null) {
                if (exclusiveOptions.id != null) {
                    LOGGER.info("get USER : `{}`", service.getUserWithId(exclusiveOptions.id));
                } else if (exclusiveOptions.userName != null) {
                    LOGGER.info("get USER(S) : `{}`", service.getUserWithName(exclusiveOptions.userName));
                } else {
                    LOGGER.info("get USER(S) : " + service.getUsers());
                }
            } else if (resourceType.equals(ResourceType.GROUP) && exclusiveOptions != null) {
                LOGGER.info("get GROUP : `{}`" + service.getGroupWithName(exclusiveOptions.userName));
            } else {
                LOGGER.info("get GROUP(S) : " + service.getGroups());
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