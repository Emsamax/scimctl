package com.worldline.mts.idm.scimctl.options;

import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jdk.jfr.Name;
import picocli.CommandLine;

@ApplicationScoped
@Unremovable
@Name("commonOption")
public class CommonOptions {

  @Inject
  ScimCtlBeansConfig config;

  @Inject
  OutputUtils outputUtils;

  @CommandLine.Option(names = { "--resource-type",
      "-t" }, description = "Resource type, user or group", converter = ResourceTypeConverter.class)
  public ResourceType resourceType;

  @CommandLine.Option(names = { "--verbose" }, description = "Enable verbose")
  public void enableVerbose(boolean verbose) {
    if (verbose) {
      outputUtils.toggleVerbose(verbose);
      outputUtils.configLoggerLevel();
      outputUtils.logMsg("verbose enabled");
    }
  }

  @CommandLine.Option(names = { "--debug" }, description = "Enable debug")
  public void enableDebug(boolean debug) {
    if (debug) {
      outputUtils.toggleDebug(debug);
      outputUtils.configLoggerLevel();
      outputUtils.logMsg("debug enabled");
    }
  }
}
