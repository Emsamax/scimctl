package com.worldline.mts.idm.scimctl.commands.common;

import java.util.HashMap;

import org.antlr.v4.parse.v4ParserException;

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


  @CommandLine.Option(names = {"--batch-size"})
  public int batchSize;

}
