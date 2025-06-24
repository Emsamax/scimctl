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

@QuarkusMain
@CommandLine.Command(name = "scim-ctl", mixinStandardHelpOptions = true, subcommands = { ScimSchema.class,
    GetCommand.class, CreateCommand.class, ImportCommand.class, UpdateCommand.class, DeleteCommand.class }, 
    header = {
     "@|green    _______________  ___    _____________ |@",
     "@|green   / __/ ___/  _/  |/  /___/ ___/_  __/ / |@",
     "@|green  _\\ \\/ /___/ // /|_/ /___/ /__  / / / /__|@",
     "@|green /___/\\___/___/_/  /_/    \\___/ /_/ /____/|@"
    })
public class ScimCtl implements QuarkusApplication {
  @Inject
  CommandLine.IFactory factory;

  @Override
  public int run(String... args) {

    var cmd = new CommandLine(this, factory);
    return cmd.execute(args);
  }
}
