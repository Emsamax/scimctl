package com.worldline.mts.idm.scimctl.commands.common;

import picocli.CommandLine;

public class FilterCommonOptions {

    @CommandLine.Option(names = {"--name", "-u"}, description = "search the User(s) with the corresponding name")
    public String userName;

  public enum ResourceType {
      USER, GROUP
  }
}
