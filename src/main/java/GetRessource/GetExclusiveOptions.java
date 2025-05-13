package GetRessource;

import picocli.CommandLine;

public class GetExclusiveOptions {
    @CommandLine.Option(names = {"--id"}, description = "User or Group id")
    String id;

    @CommandLine.Option(names = {"--username", "-u"}, description = "search the User(s) with the corresponding name")
    String userName;
}
