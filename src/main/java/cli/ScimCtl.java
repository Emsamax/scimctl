package cli;

import GetRessource.GetCommand;
import createCommand.CreateCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;
import schemaCommand.ScimSchema;

@QuarkusMain
@CommandLine.Command(
        name = "scim-ctl",
        mixinStandardHelpOptions = true,
        subcommands = {ScimSchema.class, GetCommand.class, CreateCommand.class}
)

//TODO : script configuration des variables ENV
//TODO : logger + revoir gestion des exceptions
//TODO : ne pas utiliser la concaténation (dangereux)
public class ScimCtl implements QuarkusApplication {
    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) {
       return new CommandLine(this, factory).execute(args);
    }
}
