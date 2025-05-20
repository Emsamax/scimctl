package com.worldline.mts.idm.scimctl.common;

import picocli.CommandLine;


public abstract class CommonOptions {
    @CommandLine.Option(names = {"--resource-type", "-t"},
            description = "Resource type",
            converter = ResourceTypeConverter.class)
    public FilterCommonOptions.ResourceType resourceType;
}
