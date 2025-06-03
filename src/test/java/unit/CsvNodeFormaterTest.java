package unit;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.json.JsonMapper;

import org.apache.commons.io.FilenameUtils;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * testing node restructuration
 * For log see target/surefire-reports
 */
@Order(2)
public class CsvNodeFormaterTest extends SetUpTest {

  private static final Logger LOGGER = Logger.getLogger(CsvNodeFormaterTest.class);

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
