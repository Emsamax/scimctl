package unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.NodeFormater;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jboss.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * testing node restructuration
 */
public class NodeFormaterTest {

  private Collection<List<JsonNode>> flattened = new ArrayList<>();
  private Iterator<JsonNode> expectedNestedNode;

  private static final Logger LOGGER = Logger.getLogger(NodeFormaterTest.class);

  private static ResourceStreamBuilder streamBuilder;

  private static NodeFormater nodeFormater;


  @BeforeAll
  public static void init(){
    nodeFormater = new NodeFormater(new ObjectMapper());
    streamBuilder = new ResourceStreamBuilder(new CsvMapper(), nodeFormater);
  }

  @BeforeEach
  public void setUp() throws IOException {
    //TODO : create a file for each possible case with testNestedNode as a prefix and a json file with expectedNestedNode as a prefix
    //TODO : scan class Path to retrieve all csv and json files
    //TODO : for each file compare create corresponding nodes result between csvNode and jsonNode
    //TODO : do not use resource stream builder -> this will be tested later
    //TODO : in set up create mappers.
    LOGGER.info("Setup starting");
    File csvFile = new File("src/test/resources/csv/test_users2.csv");
    File jsonFile = new File("src/test/resources/json/test_expected_users.json");
    LOGGER.info("CSV file exists: " + csvFile.exists());
    LOGGER.info("JSON file exists: " + jsonFile.exists());
    try {
      flattened = streamBuilder.fromFile(csvFile)
        .build()
        .chunk(50);
      LOGGER.info("Flattened collection initialized with size: " + flattened.size());
      JsonMapper jsonMapper = new JsonMapper();
      JsonNode jsonNode = jsonMapper.readTree(jsonFile);
      expectedNestedNode = jsonNode.iterator();

      LOGGER.info("Expected nodes initialized");
      testFlatToNestedNode();
    } catch (Exception e) {
      LOGGER.error("Error during initialization", e);
      throw e;
    }
  }

  @Test
  public void testFlatToNestedNode() {
    LOGGER.info("test flat to nested : ");
    LOGGER.info("flattened size : " + flattened.size());
    for (List<JsonNode> chunk : flattened) {
      for (JsonNode flat : chunk) {
        assumeTrue(expectedNestedNode.hasNext(), "no more expected nodes");
        assumeTrue(flattened.iterator().hasNext(), "no more flat nodes");
        var expected = expectedNestedNode.next();
        var actual = nodeFormater.flatToNestedNode(flat);

        LOGGER.info( " ===== Expected ===== : \n" + expected.toPrettyString());

        assertEquals(expected, actual, "Nested structure does not match expected result");
      }
    }
  }
}
