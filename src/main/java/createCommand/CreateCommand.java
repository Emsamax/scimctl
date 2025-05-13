package createCommand;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

//TODO : plain text
//TODO : check file extension
@CommandLine.Command(name = "create")
public class CreateCommand implements Runnable {
    @Inject
    CreateResourceService service;

    Logger LOGGER = LoggerFactory.getLogger(CreateCommand.class);

    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type (USER or GROUP)",
            converter = ResourceTypeConverter.class,
            required = true)
    private ResourceType resourceType;

    @CommandLine.Option(names = {"--data", "-d"},
            description = "JSON file for the resource",
            required = true)
    private File file;

    @Override
    public void run() {
        System.out.println("Dans la commande create");
        try {
            switch (resourceType) {
                case USER -> {
                    LOGGER.info("create USER from file path : `{}`", file);
                    service.createUser(file);
                }
                case GROUP -> {
                    LOGGER.info("create GROUP from file path : `{}`", file);
                    service.createGroup(file);
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
