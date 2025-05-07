package cli;

import GetRessource.GetCommand;
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
        subcommands = {ScimSchema.class, GetCommand.class}
)
//TODO : faire marcher GetResources
//TODO : global base url(remplace baseUrl by --target) and ScimClientConfig
//TODO : utiliser keycloak local et non image quarkus
//TODO : config global var for --target, oidc clientid/secret
//TODO : logger + gestion des exceptions
public class ScimCtl implements QuarkusApplication {
    //TODO : builder for commandes ?
    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) {
       return new CommandLine(this, factory).execute(args);
    }
}
