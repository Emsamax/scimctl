package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.worldline.mts.idm.scimctl.common.JacksonConfig;
import com.worldline.mts.idm.scimctl.common.deserializer.ResourceNodeDeserializer;
import com.worldline.mts.idm.scimctl.common.deserializer.ScimJacksonModule;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Inherited;
import java.util.Objects;
import java.util.stream.Stream;


@Unremovable
@ApplicationScoped
public class ResourceStreamBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStreamBuilder.class);

  @Inject
  CsvMapper mapper;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  JacksonConfig jacksonConfig;

  //TODO : use stream to bulk -> using stream.toList to create bulk, not smart;

  /**
   * @param file contains the data you want to create. The file must be in csv format.
   * @param <T>  scim User or Group resource type
   * @return stream of the created resource. You can use it to request bulk create resources.
   */
  public <T extends JsonNode> Stream<T> build(File file) throws IOException {
    var schema = CsvSchema.builder()
      .setUseHeader(true)
      .setColumnSeparator(',')
      .setQuoteChar('"')
      .setArrayElementSeparator("/")
      .build();

    var iterator = mapper
      .enable(CsvParser.Feature.SKIP_EMPTY_LINES)
      .enable(CsvParser.Feature.TRIM_SPACES)
      .enable(CsvParser.Feature.WRAP_AS_ARRAY)
      .readerFor(JsonNode.class)
      .with(schema)
      .readValues(file);
    return Stream.generate(() -> {
        if (iterator.hasNext()) {
          var node = iterator.next();
          return (T) node;

        }
        return null;
      })
      .takeWhile(Objects::nonNull)
      .onClose(() -> {
        try {
          iterator.close();
        } catch (IOException e) {
          LOGGER.error("Error while closing mapper : `{}`", e.getMessage());
          throw new UncheckedIOException(e);
        }
      });
  }

  //TODO : test if working

  /**
   * @param data  contains the data you want to create. The data must be in csv format.
   * @param clazz is the Object.Class you want to create from the data. User.class or Group.Class for example.
   * @param <T>   scim User or Group resource type
   * @return the created resource. You can use it to send create resource.
   */
  public <T> T createResource(String data, Class<T> clazz) throws IOException {
    T resource = null;
    try (var mapper = new CsvMapper().readerFor(clazz).readValues(data)) {
      return objectMapper.convertValue(mapper.nextValue(), clazz);
    } catch (UncheckedIOException e) {
      LOGGER.error("Error while creating resource from data : `{}`", e.getMessage());
    }
    return resource;
  }

  private void initModule() {
    ScimJacksonModule modules = new ScimJacksonModule();
    jacksonConfig.getcsvMapper().registerModule(modules);
  }
}
