import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.utils.JsonUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeAll;
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
@QuarkusTest
public class JsonUtilsTest {

  private static Collection<List<JsonNode>> flattened = new ArrayList<>();
  private static Iterator<JsonNode> expectedNestedNode;

  private static final Logger LOGGER = Logger.getLogger(JsonUtilsTest.class);

  private static JsonUtils jsonUtils;


  private static ResourceStreamBuilder stream;


  @BeforeAll
  public static void testAll() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    jsonUtils = new JsonUtils();
    if (flattened == null) {
      //init data from stream
      flattened = stream.fromFile(new File("src/main/resources/test_users.csv"))
        .build()
        .chunk(50);
      expectedNestedNode = mapper.readerFor(JsonNode.class).readValues(new File("src/main/resources/test_expected_users.json"));
    }
  }

  @Test
  public void testFlatToNestedNode() {
    for (List<JsonNode> chunk : flattened) {
      for (JsonNode flat : chunk) {
        assumeTrue(expectedNestedNode.hasNext(), "no more expected nodes");
        assumeTrue(flattened.iterator().hasNext(), "no more flat nodes");
        JsonNode expected = expectedNestedNode.next();
        JsonNode actual = jsonUtils.flatToNestedNode(flat);

        LOGGER.log(Logger.Level.DEBUG, "Nested: \n" + actual.toPrettyString());
        LOGGER.log(Logger.Level.DEBUG, "Expected: \n" + expected.toPrettyString());

        assertEquals(expected, actual, "Nested structure does not match expected result");
      }
    }
  }
}
