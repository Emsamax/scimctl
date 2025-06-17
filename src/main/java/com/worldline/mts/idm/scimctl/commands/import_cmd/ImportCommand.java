package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.worldline.mts.idm.scimctl.commands.common.CommonOptions;
import com.worldline.mts.idm.scimctl.commands.common.IOCommonOptions;
import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;
import com.worldline.mts.idm.scimctl.utils.OutputUtils;

import jakarta.inject.Inject;
import picocli.CommandLine;

import java.io.IOException;
import org.jboss.logging.Logger;

import static com.worldline.mts.idm.scimctl.commands.common.FilterCommonOptions.ResourceType.GROUP;
import static com.worldline.mts.idm.scimctl.commands.common.FilterCommonOptions.ResourceType.USER;

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
  IOCommonOptions ioOptions;

  @CommandLine.Option(names = { "--dry-run" }, description = "Enable dry run")
  public void enableDryRun(boolean dryRun) {
    if (dryRun) {
      utils.toggleDryRun(dryRun);
      utils.logMsg(LOGGER, org.jboss.logging.Logger.Level.INFO, "dry run enabled");
    }
  }

  @CommandLine.Option(names = {
      "--batch-size" }, description = "Batch size for both bulk request form cli and list response from the server")
  public void setBatchSize(Integer batchSize) {
    config.setBatchSize(batchSize);
    LOGGER.info("Batch changed to : " + batchSize);
  }

  @Override
  public void run() {
    if (ioOptions == null) {
      System.err.println("must precise a file path");
    } else {
      try {
        switch (options.resourceType) {
          case USER -> {
            utils.logMsg(LOGGER, Logger.Level.INFO, "import USER from file path : " + ioOptions.path);
            service.importResource(ioOptions.path, USER);
          }

          case GROUP -> {
            utils.logMsg(LOGGER, Logger.Level.INFO, "import GROUP from file path : `" + ioOptions.path);
            service.importResource(ioOptions.path, GROUP);
          }
        }

      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    }
  }

}
