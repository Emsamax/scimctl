package com.worldline.mts.idm.scimctl;

import com.worldline.mts.idm.scimctl.commands.delete_cmd.DeleteCommand;
import com.worldline.mts.idm.scimctl.commands.get_cmd.GetCommand;

import org.jboss.logging.Logger;

import com.worldline.mts.idm.scimctl.commands.create_cmd.CreateCommand;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ImportCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

import picocli.CommandLine;

import com.worldline.mts.idm.scimctl.commands.schema_cmd.ScimSchema;
import com.worldline.mts.idm.scimctl.commands.update_cmd.UpdateCommand;

@QuarkusMain
@CommandLine.Command(name = "scim-ctl", mixinStandardHelpOptions = true, subcommands = { ScimSchema.class,
    GetCommand.class, CreateCommand.class, ImportCommand.class, UpdateCommand.class, DeleteCommand.class })
public class ScimCtl implements QuarkusApplication {
  //TODO check server response code more accuratelty

  @Inject
  CommandLine.IFactory factory;


  @Override
  public int run(String... args) {
    return new CommandLine(this, factory).execute(args);
  }
}
