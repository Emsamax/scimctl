package GetRessource;

import createCommand.ResourceType;
import createCommand.ResourceTypeConverter;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;


@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

    @Inject
    GetResourceService service;

    private static Logger LOGGER = LoggerFactory.getLogger(GetCommand.class);

    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type",
            converter = ResourceTypeConverter.class,
            required = true)
    private ResourceType resourceType;

    @CommandLine.Option(names = {"--id"}, description = "User or Group id")
    private String id;

    @CommandLine.Option(names = {"--username", "-u"}, description = "search the User(s) with the corresponding name")
    private boolean userName;

    @Override
    public void run() {
        try {
            if (resourceType.equals(ResourceType.USER) && id != null) {
                LOGGER.info("get USER : `{}`", service.getUserResource(id));
            } else if (resourceType.equals(ResourceType.GROUP)) {
                LOGGER.info("get GROUP : " + service.getUserResource());
            } else if (resourceType.equals(ResourceType.USER)) {
                LOGGER.info("get USER(S) : " + service.getUserResource());
            }
        } catch (BadRequestException e){
            LOGGER.error("bad request : `{}`",  e.getMessage());
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e){
            LOGGER.error("id does not exist : `{}`",  e.getMessage());
            System.err.println(e.getMessage());
        }
    }

}
