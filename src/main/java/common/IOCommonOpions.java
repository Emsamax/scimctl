package common;

import picocli.CommandLine;

/**
 * needs to be used with exclusive = true (default) and multiciplity = 0 (only one option required)
 * cant search by id if username is specified
 */
public class IOCommonOpions {
    @CommandLine.Option(names = {"--file", "-f"},
            description = "JSON file for the resource")
    public String path;

    @CommandLine.Option(names = {"--data", "-d"},
            description = "Json of the resource directly in console")
    public String text;
}
