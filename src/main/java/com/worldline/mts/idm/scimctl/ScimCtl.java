package com.worldline.mts.idm.scimctl;

import com.worldline.mts.idm.scimctl.commands.delete_cmd.DeleteCommand;
import com.worldline.mts.idm.scimctl.commands.get_cmd.GetCommand;
import com.worldline.mts.idm.scimctl.commands.create_cmd.CreateCommand;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ImportCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;
import com.worldline.mts.idm.scimctl.commands.schema_cmd.ScimSchema;
import com.worldline.mts.idm.scimctl.commands.update_cmd.UpdateCommand;

import java.util.logging.LogManager;
import java.util.logging.Logger;

@QuarkusMain
@CommandLine.Command(
  name = "scim-ctl",
  mixinStandardHelpOptions = true,
  subcommands = {ScimSchema.class, GetCommand.class, CreateCommand.class, ImportCommand.class, UpdateCommand.class, DeleteCommand.class}
)

//TODO : script configuration des variables ENV
public class ScimCtl implements QuarkusApplication {
  @Inject
  CommandLine.IFactory factory;

  private static final Logger LOGGER = LogManager.getLogManager().getLogger("scim-ctl");

  @Override
  public int run(String... args) {
    LOGGER.info("Start the CLI");
    return new CommandLine(this, factory).execute(args);
  }
}
