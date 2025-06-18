package com.worldline.mts.idm.scimctl.commands.common;

import picocli.CommandLine;

public class SearchCommonOption {
  @CommandLine.Option(names = { "--id" }, description = "User or Group id")
  public String id;
}
