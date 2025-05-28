package integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.config.ScimCtlConfig;
import com.worldline.mts.idm.scimctl.utils.NodeFormater;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * integration test for beans managed by quarkus
 */
@QuarkusTest
public class ScimCtlConfigTest {

  @Inject
  ScimCtlConfig config;

  private NodeFormater formatter;

  private ResourceStreamBuilder resourceStreamBuilder;

  @BeforeEach
  public void testRetrieveBeans() {
    assertNotNull(config);
    assertNotNull(config.getObjectMapper());
    assertNotNull(config.getCsvMapper());
    var csvMapper = config.getCsvMapper();
    assertNotNull(config.getNodeFormater(csvMapper));
    var nodeFormater = config.getNodeFormater(csvMapper);
    this.formatter = nodeFormater;
    assertNotNull(config.getResourceStreamBuilder(csvMapper, nodeFormater));
    var stream = config.getResourceStreamBuilder(csvMapper, nodeFormater);
    this.resourceStreamBuilder = stream;
  }

  @Test
  @Order(1)
  public void testNodeFormater(JsonNode flatNode, JsonNode expectedNode) {
    assertEquals(this.formatter.flatToNestedNode(flatNode), expectedNode);
  }


  @Test
  @Order(2)
  public void testResourceStreamBuilder(String csfFilePath, String jsonFilePath) {
    //TODO
  }
}
