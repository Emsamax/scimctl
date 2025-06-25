package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;
import com.worldline.mts.idm.scimctl.options.CommonOptions;
import com.worldline.mts.idm.scimctl.options.IOOptions;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import jakarta.inject.Inject;
import picocli.CommandLine;

import java.io.IOException;

import org.jboss.logging.Logger;

@CommandLine.Command(name = "import")
public class ImportCommand implements Runnable {
  @Inject
  ImportService service;

  @Inject
  ScimCtlBeansConfig config;

  @Inject
  Logger LOGGER;

  @CommandLine.Mixin
  CommonOptions options;

  @Inject
  OutputUtils utils;
  /**
   * Force the user to specify either the path to the JSON file or write the JSON
   * data directly into the console.
   */
  @CommandLine.ArgGroup(heading = "Resource creation options:%n")
  IOOptions ioOptions;

  @CommandLine.Option(names = { "--dry-run" }, description = "Enable dry run")
  public void enableDryRun(boolean dryRun) {
    if (dryRun) {
      utils.toggleDryRun(dryRun);
      utils.logMsg("dry run enabled");
    }
  }

  @CommandLine.Option(names = {
      "--batch-size" }, description = "Batch size for both size of bulk request send by the cli and size of list request from the server")
  public void setBatchSize(Integer batchSize) {
    config.setBatchSize(batchSize);
    LOGGER.info("Batch changed to : " + batchSize);
  }

  @Override
  public void run() {
    if (options.resourceType == null) {
      System.err.print("you must percise a resource type\n");
      return;
    }
    if (ioOptions == null) {
      System.err.println("must precise a file path");
      return;
    }
    try {
      switch (options.resourceType) {
        case USER -> utils.logMsg("import USER from file path : " + ioOptions.path);
        case GROUP -> utils.logMsg("import GROUP from file path : `" + ioOptions.path);
      }
      service.importResource(ioOptions.path, options.resourceType);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}
