package com.worldline.mts.idm.scimctl;

import com.worldline.mts.idm.scimctl.commands.delete_cmd.DeleteCommand;
import com.worldline.mts.idm.scimctl.commands.get_cmd.GetCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
  @Inject
  CommandLine.IFactory factory;

  @Override
  public int run(String... args) {
    createCache();
    return new CommandLine(this, factory).execute(args);
  }

  /**
   * create .token in the in /tmp with chmod +w
   * if already present do nothing
   */
  private void createCache() {
    // Récupère le répertoire en cours d'exécution
    String projectRoot = System.getProperty("user.dir");
    var PATH = projectRoot + "/scripts/cache.sh";
    var processBuilder = new ProcessBuilder();
    processBuilder.command("bash", "-c", PATH);
    try {
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

}
