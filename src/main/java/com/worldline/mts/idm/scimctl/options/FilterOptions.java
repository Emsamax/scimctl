package com.worldline.mts.idm.scimctl.options;

import picocli.CommandLine;

public class FilterOptions {

    @CommandLine.Option(names = {"--name", "-u"}, description = "search the resource(s) with the corresponding name")
    public String userName;

  
}
