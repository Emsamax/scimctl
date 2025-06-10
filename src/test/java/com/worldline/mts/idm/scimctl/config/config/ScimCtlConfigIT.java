package com.worldline.mts.idm.scimctl.config.config;

import com.worldline.mts.idm.scimctl.config.ScimCtlConfig;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * integration test for beans managed by quarkus
 */
@QuarkusTest
public class ScimCtlConfigIT {

  @Inject
  ScimCtlConfig config;

  @Test
  public void testRetrieveBeans() {
    assertNotNull(config);
    assertNotNull(config.getObjectMapper());
    assertNotNull(config.getNodeFormater());
    var nodeFormater = config.getNodeFormater();
    assertNotNull(config.getResourceStreamBuilder(nodeFormater));
    var stream = config.getResourceStreamBuilder(nodeFormater);
    assertNotNull(stream);
  }

}
