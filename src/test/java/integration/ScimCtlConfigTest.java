package integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import com.worldline.mts.idm.scimctl.config.ScimCtlConfig;
import com.worldline.mts.idm.scimctl.utils.NodeFormater;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
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

  private static final Logger LOGGER = Logger.getLogger(ScimCtlConfigTest.class);

  @Test
  @Order(0)
  public void testRetrieveBeans() {
    assertNotNull(config);
    assertNotNull(config.getObjectMapper());
    assertNotNull(config.getCsvMapper());
    var csvMapper = config.getCsvMapper();
    assertNotNull(config.getNodeFormater());
    var nodeFormater = config.getNodeFormater();
    assertNotNull(config.getResourceStreamBuilder(csvMapper, nodeFormater));
    var stream = config.getResourceStreamBuilder(csvMapper, nodeFormater);
    assertNotNull(stream);
    LOGGER.info("All beans instantiated");
  }

}
