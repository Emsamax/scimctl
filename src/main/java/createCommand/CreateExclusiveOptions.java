package createCommand;

import picocli.CommandLine;

public class CreateExclusiveOptions {
    @CommandLine.Option(names = {"--path", "-p"},
            description = "JSON file for the resource")
    String path;

    @CommandLine.Option(names = {"--text", "-t"},
            description = "Json of the resource directly in console")
    String text;
}
