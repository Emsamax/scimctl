package com.worldline.mts.idm.scimctl.config.config;

import com.worldline.mts.idm.scimctl.config.ScimCtlConfig;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * integration test for beans managed by quarkus
 */
@QuarkusTest
public class ScimCtlConfigIT {

  @Inject
  ScimCtlConfig config;

  private static final Logger LOGGER = Logger.getLogger(ScimCtlConfigIT.class);

  @Test
  public void testRetrieveBeans() {
    assertNotNull(config);
    //assertNotNull(config.getObjectMapper());
    //assertNotNull(config.getCsvMapper());
    //var csvMapper = config.getCsvMapper();
    assertNotNull(config.getNodeFormater());
    var nodeFormater = config.getNodeFormater();
    //assertNotNull(config.getResourceStreamBuilder(csvMapper, nodeFormater));
    //var stream = config.getResourceStreamBuilder(csvMapper, nodeFormater);
    //assertNotNull(stream);
  }

}
