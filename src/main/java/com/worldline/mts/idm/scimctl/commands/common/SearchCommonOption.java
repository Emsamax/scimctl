package com.worldline.mts.idm.scimctl.commands.common;

import picocli.CommandLine;

/**
 * needs to be used with exclusive = true (default) and multiciplity = 1 (= all
 * options required)
 * cant search by id if username is specified
 */
public class SearchCommonOption {
  @CommandLine.Option(names = { "--id" }, description = "User or Group id")
  public String id;
}
