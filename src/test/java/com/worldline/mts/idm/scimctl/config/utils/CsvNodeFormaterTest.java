package com.worldline.mts.idm.scimctl.config.utils;

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
  @Test
  public void testFlatToNestedNodes() {
    if (fileIterator.hasNext()) {
      currentInputFile = new File(fileIterator.next().toUri());
      Optional<Path> filePath = expectedFiles
        .stream()
        .filter(filePathStr -> {
          var jsonFileName = FilenameUtils.removeExtension(filePathStr.getFileName().toString());
          var matcher = FilenameUtils.removeExtension(currentInputFile.getName());
          return jsonFileName.equals(matcher);
        })
        .findFirst();
      if (filePath.isPresent()) {
        expectedFile = new File(filePath.get().toUri());
        testFlatToNestedNode();
      } else {
        LOGGER.warn("No JSON files found");
      }
    }
  }

  @Test
  protected void testFlatToNestedNode() {
    try {
      Collection<List<JsonNode>> flattened = streamBuilder.fromFile(currentInputFile)
                                                          .build()
                                                          .chunk(50);
      JsonMapper jsonMapper = new JsonMapper();
      JsonNode jsonNode = jsonMapper.readTree(expectedFile);
      Iterator<JsonNode> expectedNestedNode = jsonNode.iterator();
      for (List<JsonNode> chunk : flattened) {
        for (JsonNode flat : chunk) {
          assertNotNull(expectedNestedNode, "no more expected nodes");
          assertNotNull(flattened.iterator(), "no more flat nodes");
          var expected = expectedNestedNode.next().toPrettyString();
          var actual = nodeFormater.flatToNestedNode(flat).toPrettyString();
          assertEquals(expected, actual, "Nested structure does not match expected result");
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error during initialization", e);
    }
  }
}
