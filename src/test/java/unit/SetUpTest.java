package unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import org.apache.commons.io.FilenameUtils;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class SetUpTest {
  private static final Logger LOGGER = Logger.getLogger(SetUpTest.class);

  public static ResourceStreamBuilder streamBuilder;

  public static NodeFormater nodeFormater;

  public File jsonFile;

  public File csvFile;

  public static List<Path> csvFiles;

  public static List<Path> jsonFiles;

  public static Iterator<Path> fileIterator;

  @BeforeAll
  public static void setUp() throws IOException {
    LOGGER.info("Setup starting");
    nodeFormater = new NodeFormater(new ObjectMapper());
    streamBuilder = new ResourceStreamBuilder(new CsvMapper(), nodeFormater);
    jsonFiles = new ArrayList<>();
    csvFiles = new ArrayList<>();
    try (Stream<Path> stream = Files.walk(Paths.get("src/test/resources"), Integer.MAX_VALUE)) {
      stream
        .filter(file -> !Files.isDirectory(file))
        .forEach(file -> {
          if (file.toString().endsWith(".csv")) csvFiles.add(file);
          if (file.toString().endsWith(".json")) jsonFiles.add(file);
        });
    }
    fileIterator = csvFiles.iterator();
    csvFiles.forEach(System.out::println);
    jsonFiles.forEach(System.out::println);
  }

}
