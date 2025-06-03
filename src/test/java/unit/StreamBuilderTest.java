package unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.SetUpTest.*;

@Order(1)
public class StreamBuilderTest {
  private static final Logger LOGGER = Logger.getLogger(StreamBuilderTest.class);
  private final ResourceStreamBuilder stream;

  public StreamBuilderTest() {
    super();
    this.stream = streamBuilder;
  }

  @Test
  @Order(0)
  void testThrowException() {
    LOGGER.info("test throw");
    stream.setFile(null);
    assertThrows(IllegalStateException.class, stream::build);
    LOGGER.info("throw passed");
  }

  @Test
  @Order(1)
  void testFromFile() {
    LOGGER.info("test fromFile");
    var file = csvFiles.getFirst().toFile();
    stream.fromFile(file);
    assertEquals(stream.getFile(), file);
    LOGGER.info("FromFile passed");
  }

  @Test
  @Order(2)
  void testBuild() {
    LOGGER.info("test build");
    try {
      stream.build();
      assertNotNull(stream);
    } catch (IOException e) {
      LOGGER.log(Logger.Level.ERROR, "Error building the stream");
    }
    LOGGER.info("build passed");
  }

  @Test
  @Order(3)
  void chunk() {
    LOGGER.info("test chunk");
    var size = 1;
    var chunks = stream.chunk(size);
    for (List<JsonNode> chunk : chunks) {
      LOGGER.info("size = " + size + " : " + chunk.toString());
      assertEquals(size, chunk.size());
    }
    LOGGER.info("chunk passed");
  }
}

