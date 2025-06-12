package com.worldline.mts.idm.scimctl.commands.common;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jdk.jfr.Name;
import picocli.CommandLine;


@ApplicationScoped
@Unremovable
@Name("commonOption")
public class CommonOptions {
  @CommandLine.Option(names = { "--resource-type","-t" },
  description = "Resource type", converter = ResourceTypeConverter.class)
  public FilterCommonOptions.ResourceType resourceType;

  @CommandLine.Option(names = { "--verbose", "-v" }, description = "verbosity")
  public boolean verbose;

}
