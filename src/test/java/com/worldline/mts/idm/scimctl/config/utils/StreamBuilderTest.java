package com.worldline.mts.idm.scimctl.config.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StreamBuilderTest extends SetUpTest {
  private static final Logger LOGGER = Logger.getLogger(StreamBuilderTest.class);

  public StreamBuilderTest() {
    super();
  }
/*
  @Test
  @Order(1)
  void testThrowException() {
    streamBuilder.setFile(null);
    assertThrows(IllegalStateException.class, streamBuilder::build);
  }

  @Test
  @Order(2)
  void testFromFile() {
    var file = data.getFirst().input();
    System.out.println(file);
    streamBuilder.fromFile(file);
    assertEquals(streamBuilder.getFile(), file);
  }

  @Test
  @Order(3)
  void testBuild() {
    try {
      streamBuilder.fromFile(data.getFirst().input()).build();
      assertNotNull(streamBuilder);
    } catch (IOException e) {
      LOGGER.log(Logger.Level.ERROR, "Error building the stream");
    } catch (IllegalStateException e) {
      LOGGER.error(e.getMessage());
    }
  }

  @Test
  @Order(4)
  void chunk() {
    try {
      var size = 1;
      var chunks = streamBuilder.fromFile(data.getFirst().input()).build().chunk(size);
      for (List<JsonNode> chunk : chunks) {
        LOGGER.info("size = " + size + " : " + chunk.toString());
        assertEquals(size, chunk.size());
      }
    } catch (IllegalStateException | IOException e) {
      LOGGER.error(e.getMessage());
    }

  }

 */
}

