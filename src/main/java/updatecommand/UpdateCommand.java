package updatecommand;

import common.IOCommonOptions;
import common.SearchCommonOption;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(name = "update")
public class UpdateCommand implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCommand.class);
    @Inject
    UpdateService service;
    /**
     * force the user to specify both the id and user.json .
     */
    @CommandLine.ArgGroup(heading = "Update options:%n", exclusive = false, multiplicity = "1")
    SearchCommonOption options;

    @CommandLine.ArgGroup(heading = "IO options:%n", exclusive = false, multiplicity = "1")
    IOCommonOptions Options;


    @Override
    public void run() {
        try {
            switch (options.resourceType) {
                case USER -> {
                    if (options.id != null && Options.path != null) {
                        service.updateUser(options.id, Options.path);
                    }
                }
            }
        } catch (IOException e){
            LOGGER.error("error reading the file : `{}`", e.getMessage());
        }


    }
}
