package com.worldline.mts.idm.scimctl.common;

import picocli.CommandLine;

/**
 * needs to be used with exclusive = true (default) and multiciplity = 0 (only one option required)
 * can't search by id if username is specified
 */
public class IOCommonOptions {
    @CommandLine.Option(names = {"--file", "-f"},
            description = "CSV file for the resource")
    public String path;

    @CommandLine.Option(names = {"--data", "-d"},
            description = "Json of the resource directly in console")
    public String text;
}
