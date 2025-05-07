package cli;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;
import schemaCommand.ScimSchema;

@QuarkusMain
@CommandLine.Command(
        name = "scim-ctl",
        mixinStandardHelpOptions = true,
        subcommands = {ScimSchema.class}
)

public class ScimCtl implements QuarkusApplication {
    //TODO : builder for commandes ?

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) {
       return new CommandLine(this, factory).execute(args);
    }
}
