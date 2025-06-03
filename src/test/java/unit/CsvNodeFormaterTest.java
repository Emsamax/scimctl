package unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.strategy.NodeFormater;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.jboss.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * testing node restructuration
 * For log see target/surefire-reports
 */
public class CsvNodeFormaterTest {

  private static final Logger LOGGER = Logger.getLogger(CsvNodeFormaterTest.class);

  private static ResourceStreamBuilder streamBuilder;

  private static NodeFormater nodeFormater;

  private File jsonFile;

  private File csvFile;

  private static List<Path> csvFiles;

  private static List<Path> jsonFiles;

  private static Iterator<Path> fileIterator;

  /**
   * iterate on all csvFiles and matches the name of the file in jsonFiles to get the expected file
   */
  @BeforeEach
  public void changeTestCase() {
    if (fileIterator.hasNext()) {
      csvFile = new File(fileIterator.next().toUri());
      Optional<Path> filePath = jsonFiles
        .stream()
        .filter(filePathStr -> {
          var jsonFileName = FilenameUtils.removeExtension(filePathStr.getFileName().toString());
          var matcher = FilenameUtils.removeExtension(csvFile.getName());
          LOGGER.info("CSV file name = " + matcher + " JSON file name = " + jsonFileName);
          return jsonFileName.equals(matcher);
        })
        .findFirst();
      LOGGER.info("CSV file : " + csvFile.getName());
      if (filePath.isPresent()) {
        jsonFile = new File(filePath.get().toUri());
        LOGGER.info("JSON file : " + jsonFile.getName());
      } else {
        LOGGER.warn("No JSON files found");
      }
    }
  }

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

  @RepeatedTest(value = 5)
  public void testFlatToNestedNode() {
    try {
      Collection<List<JsonNode>> flattened = streamBuilder.fromFile(csvFile)
                                                          .build()
                                                          .chunk(50);
      LOGGER.info("Flattened collection initialized with size: " + flattened.size());
      JsonMapper jsonMapper = new JsonMapper();
      JsonNode jsonNode = jsonMapper.readTree(jsonFile);
      Iterator<JsonNode> expectedNestedNode = jsonNode.iterator();
      LOGGER.info("Expected nodes initialized");
      LOGGER.info("test flat to nested : ");
      LOGGER.info("flattened size : " + flattened.size());
      for (List<JsonNode> chunk : flattened) {
        for (JsonNode flat : chunk) {
          assertNotNull(expectedNestedNode, "no more expected nodes");
          assertNotNull(flattened.iterator(), "no more flat nodes");
          var expected = expectedNestedNode.next().toPrettyString();
          var actual = nodeFormater.flatToNestedNode(flat).toPrettyString();
          LOGGER.info(" ===== Expected ===== : \n" + expected);
          assertEquals(expected, actual, "Nested structure does not match expected result");
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error during initialization", e);
    }
  }
}
