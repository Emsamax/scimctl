package com.worldline.mts.idm.scimctl.commands.common;

import org.jboss.logging.Logger;
import org.stringtemplate.v4.compiler.CodeGenerator.primary_return;

import com.worldline.mts.idm.scimctl.config.ScimCtlBeansConfig;

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

  @CommandLine.Option(names = { "--resource-type",
      "-t" }, description = "Resource type", converter = ResourceTypeConverter.class)
  public FilterCommonOptions.ResourceType resourceType;

  @CommandLine.Option(names = { "--batch-size" })
  public void setBatchSize(Integer batchSize) {
    config.setBatchSize(batchSize);
    LOGGER.info("Batch changed to : " + batchSize);
  }
}
