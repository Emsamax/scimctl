package com.worldline.mts.idm.scimctl.options;

import picocli.CommandLine;

public class SearchOptions {
  @CommandLine.Option(names = { "--id" }, description = "User or Group id")
  public String id;
}
