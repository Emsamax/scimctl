package createCommand;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import picocli.CommandLine;

import java.io.File;

// TODO : lire user depuis fichier et spécifier chemin dans la commande
@CommandLine.Command(name = "create")
public class CreateCommand implements Runnable {
    @Inject
    CreateResourceService service;

    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type (USER or GROUP)",
            converter = ResourceTypeConverter.class,
            required = true)
    private ResourceType resourceType;

    @CommandLine.Option(names = {"--data"},
            description = "JSON file for the resource",
            required = true)
    private File file;

    @Override
    public void run() {
        System.out.println("Dans la commande create");
        try {
            // Validation et traitement selon le type de ressource
            switch (resourceType) {
                case USER -> {
                    service.createUser(file);
                }
                case GROUP -> {
                    service.createGroup(file);
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("error parsing JSON data :" + e.getMessage());
        } catch (BadRequestException e) {
            System.err.println("error :" + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("error isn't describe in RFC7644 :" + e.getMessage());
        } catch (Exception e) {
            System.err.println("error : " + e.getMessage());
        }
    }


}
