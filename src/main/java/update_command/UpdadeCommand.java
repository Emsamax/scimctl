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

    @CommandLine.ArgGroup(heading = "Update options:%n")
    SearchCommonOption options;

    @Override
    public void run() {

    }
}
