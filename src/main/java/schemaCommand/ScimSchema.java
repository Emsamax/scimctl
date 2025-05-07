package schemaCommand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import picocli.CommandLine;

@ApplicationScoped
@CommandLine.Command(name = "schema")
public class ScimSchema implements Runnable{

    @Inject
    SchemaService schemaService;

    @Override
    public void run() {
        System.out.println("DANS LA COMMANDE SCHEMA");
        System.out.println(schemaService.getSchema());
    }
}
