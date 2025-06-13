package com.worldline.mts.idm.scimctl.commands.common;

import org.jboss.logging.Logger;

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

  private static final Logger LOGGER = Logger.getLogger(CommonOptions.class);

  @Inject
  ScimCtlBeansConfig config;

  @Inject
  OutputUtils outputUtils;

  @CommandLine.Option(names = { "--resource-type",
      "-t" }, description = "Resource type", converter = ResourceTypeConverter.class)
  public FilterCommonOptions.ResourceType resourceType;

  @CommandLine.Option(names = {
      "--batch-size" }, description = "Batch size for both bulk request form cli and list response from the server")
  public void setBatchSize(Integer batchSize) {
    config.setBatchSize(batchSize);
    LOGGER.info("Batch changed to : " + batchSize);
  }

  @CommandLine.Option(names = { "--dry-run" }, description = "Enable dry run")
  public void enableDryRun(boolean dryRun) {
    if (dryRun) {
      outputUtils.toggleDryRun(dryRun);
      outputUtils.logMsg(LOGGER, org.jboss.logging.Logger.Level.INFO, "dry run enabled");
    }
  }

  @CommandLine.Option(names = { "--verbose" }, description = "Enable verbose")
  public void enableVerbose(boolean verbose) {
    if (verbose) {
      outputUtils.toggleVerbose(verbose);
      outputUtils.logMsg(LOGGER, org.jboss.logging.Logger.Level.INFO, "verbose enabled");
    }
  }
}
