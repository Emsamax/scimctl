package importcommand;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.IOCommonOptions;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import common.ResourceType;
import common.ResourceTypeConverter;

import java.io.IOException;

import static common.ResourceType.USER;

@CommandLine.Command(name = "import")
public class ImportCommand implements Runnable {
    @Inject
    ImportService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportCommand.class);

    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type (USER or GROUP)",
            converter = ResourceTypeConverter.class,
            required = true)
    private ResourceType resourceType;

    /**
     * Force the user to specify either the path to the JSON file or write the JSON data directly into the console.
     */
    @CommandLine.ArgGroup(heading = "Resource creation options:%n", multiplicity = "1")
    IOCommonOptions Options;

    @Override
    public void run() {
        try {
            switch (resourceType) {
                case USER -> {
                    LOGGER.info("import USER from file path : `{}`", Options.path);
                    service.importUser(Options.path);
                }
                case GROUP -> {
                    LOGGER.info("import GROUP from file path : `{}`", Options.path);
                    service.importGroup(Options.path);
                }
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("error parsing data `{}`", e.getMessage());
            System.err.println("error parsing data :" + e.getMessage());
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
