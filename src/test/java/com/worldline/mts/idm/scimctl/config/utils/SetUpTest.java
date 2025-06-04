package com.worldline.mts.idm.scimctl.config.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SetUpTest {
  private static final Logger LOGGER = Logger.getLogger(SetUpTest.class);

  public static ResourceStreamBuilder streamBuilder;

  public static NodeFormater nodeFormater;

  public File expectedFile;

  public File currentInputFile;

  public static List<Path> inputFiles;

  public static List<Path> expectedFiles;

  public static Iterator<Path> fileIterator;

  @BeforeAll
  public static void setUp() throws IOException {
    LOGGER.info("Setup starting");
    nodeFormater = new NodeFormater(new ObjectMapper());
    streamBuilder = new ResourceStreamBuilder(new CsvMapper(), nodeFormater);
    expectedFiles = new ArrayList<>();
    inputFiles = new ArrayList<>();
    try (var stream = Files.walk(Paths.get("src/test/resources"), Integer.MAX_VALUE)) {
      stream
        .filter(file -> !Files.isDirectory(file))
        .forEach(file -> {
          if (file.toString().endsWith(".csv")) inputFiles.add(file);
          if (file.toString().endsWith(".json")) expectedFiles.add(file);
        });
    }
    fileIterator = inputFiles.iterator();
  }
}
