package com.worldline.mts.idm.scimctl.options;

import picocli.CommandLine;

/**
 * needs to be used with exclusive = true (default) and multiciplity = 0 (only
 * one option required)
 * can't search by id if username is specified
 */
public class IOOptions {
  @CommandLine.Option(names = { "--file", "-f" }, description = "path to the CSV file to import")
  public String path;

  @CommandLine.Option(names = { "--data", "-d" }, description = "JSON content")
  public String text;
}
