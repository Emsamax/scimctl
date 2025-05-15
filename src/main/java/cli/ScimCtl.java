package cli;

import get_command.GetCommand;
import create_command.CreateCommand;
import importcommand.ImportCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;
import schema_command.ScimSchema;

import java.util.logging.LogManager;
import java.util.logging.Logger;

@QuarkusMain
@CommandLine.Command(
        name = "scim-ctl",
        mixinStandardHelpOptions = true,
        subcommands = {ScimSchema.class, GetCommand.class, CreateCommand.class, ImportCommand.class}
)

//TODO : script configuration des variables ENV
public class ScimCtl implements QuarkusApplication {
    @Inject
    CommandLine.IFactory factory;

    private static final Logger LOGGER = LogManager.getLogManager().getLogger("scim-ctl");

    @Override
    public int run(String... args) {
        LOGGER.info("Start the CLI");
        return new CommandLine(this, factory).execute(args);
    }
}
