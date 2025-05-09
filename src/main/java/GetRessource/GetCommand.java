package GetRessource;

import createCommand.ResourceType;
import createCommand.ResourceTypeConverter;
import jakarta.inject.Inject;
import picocli.CommandLine;


@CommandLine.Command(name = "get")
public class GetCommand implements Runnable {

    @Inject
    GetResourceService service;

    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type",
            converter = ResourceTypeConverter.class,
            required = true)
    private ResourceType resourceType;

    @CommandLine.Option(names = {"--id"}, description = "User or Group id")
    private String id;

    @CommandLine.Option(names = {"--username", "-u"}, description = "search the User(s) with the corresponding name")
    private String userName;

    @Override
    public void run() {
        System.out.println("Dans la commande get");
        if(resourceType.equals(ResourceType.USER) && id != null) {
            System.out.println("USER : " + service.getUserResource(id));
        }else if(resourceType.equals(ResourceType.GROUP)){

        }else if(resourceType.equals(ResourceType.USER) && userName == null && userName == null){
            System.out.println("USERS : " + service.getUserResource());
        }
        //todo : throw exceptions
        //todo : test cmd


    }

}
