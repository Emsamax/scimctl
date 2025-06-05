package com.worldline.mts.idm.scimctl.config.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SetUpTest {
  /*
  private static final Logger LOGGER = Logger.getLogger(SetUpTest.class);

  public static ResourceStreamBuilder streamBuilder;

  public static NodeFormater nodeFormater;

  public File currentExpectedFile;

  public File currentInputFile;

  public static List<TestCasesFiles> data;

  public static Iterator<TestCasesFiles> it;

  public record TestCasesFiles(File input, File expected) {
  }

  @BeforeAll
  public static void setUp() throws IOException {
    nodeFormater = new NodeFormater(new ObjectMapper());
    streamBuilder = new ResourceStreamBuilder(new CsvMapper(), nodeFormater);
    data = new ArrayList<>();
    try (var stream = Files.walk(Paths.get("src/test/resources"), Integer.MAX_VALUE)) {
      stream
        .filter(path -> !Files.isDirectory(path))
        .filter(path -> path.toString().endsWith(".csv"))
        .forEach(path -> {
          var fileName = path.toString().split("\\.")[0];
          Path newFile = Path.of(fileName + ".json");
          data.add(new TestCasesFiles(new File(path.toUri()), new File(newFile.toUri())));
        });
    }
    it = data.iterator();
  }
  */
}
