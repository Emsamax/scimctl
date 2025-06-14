package com.worldline.mts.idm.scimctl.config.utils;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.json.JsonMapper;

import com.worldline.mts.idm.scimctl.utils.strategy.NodeWrapper;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

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
   * iterate on all csvFiles and matches the name of the file in jsonFiles to get
   * the expected file
   */
  @Test
  public void testFlatToNestedNodes() {

    while (it.hasNext()) {
      var tuple = it.next();
      currentInputFile = tuple.input();
      currentExpectedFile = tuple.expected();
      testFlatToNestedNode();
    }
  }

  @Test
  protected void testFlatToNestedNode() {
    try {
      Collection<List<NodeWrapper>> flattened = streamBuilder.fromFile(currentInputFile)
          .build()
          .chunk(50);
      JsonMapper jsonMapper = new JsonMapper();
      JsonNode jsonNode = jsonMapper.readTree(currentExpectedFile);
      Iterator<JsonNode> expectedNestedNode = jsonNode.iterator();
      for (List<NodeWrapper> chunk : flattened) {
        for (NodeWrapper flat : chunk) {
          assertNotNull(expectedNestedNode, "no more expected nodes");
          assertNotNull(flattened.iterator(), "no more flat nodes");
          var expected = expectedNestedNode.next().toPrettyString();
          var actual = nodeFormater.flatToNestedNode(flat, streamBuilder.getHeader()).getJsonNode().get()
              .toPrettyString();
          assertEquals(expected, actual, "Nested structure does not match expected result");
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error during initialization", e);
    }
  }
}
