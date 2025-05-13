package common;

import picocli.CommandLine;
import resource_type.ResourceType;
import resource_type.ResourceTypeConverter;


public abstract class CommonOptions {
    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type",
            converter = ResourceTypeConverter.class)
    public ResourceType resourceType;
}
