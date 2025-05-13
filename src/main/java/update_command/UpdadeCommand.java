package update_command;

import common.SearchCommonOption;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command
public class UpdadeCommand implements Runnable{

    @Inject
    UpdateService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdadeCommand.class);

    /**
     * force the user to specify either the id and user.json .
     */
    @CommandLine.ArgGroup(heading = "Update options:%n", multiplicity = "1")
    SearchCommonOption options;

    @Override
    public void run() {

    }
}
