package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.stream.Stream;


@Named("serviceUtils")
@Unremovable
@ApplicationScoped
public class ServiceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUtils.class);

    @Inject
    ObjectMapper objectMapper;


    //TODO : use stream to bulk -> using stream.toList to create bulk, not smart;

    /**
     * @param file  contains the data you want to create. The file must be in csv format.
     * @param clazz is the Object.class you want to create from the file. User.class or Group.class for example.
     * @param <T>   scim User or Group resource type
     * @return stream of the created resource. You can use it to request bulk create resources.
     */
    public <T> Stream<T> createResource(File file, Class<T> clazz) throws IOException {
        var schema = CsvSchema.builder()
                .setUseHeader(true)
                .build();
        var mapper = new CsvMapper()
                .enable(CsvParser.Feature.SKIP_EMPTY_LINES)
                .enable(CsvParser.Feature.TRIM_SPACES)
                .readerFor(clazz)
                .with(schema)
                .readValues(file);
        return Stream.generate(() -> {
                    try {
                        if (mapper.hasNextValue()) {
                            //cast impossible
                            var resource = objectMapper.convertValue(mapper.nextValue(), clazz);
                            return resource;
                        } else return null;
                    } catch (IOException e) {
                        LOGGER.error("Error while creating resource from file : `{}`", e.getMessage());
                        throw new UncheckedIOException(e);
                    }
                })
                .takeWhile(Objects::nonNull)
                .onClose(() -> {
                    try {
                        mapper.close();
                    } catch (IOException e) {
                        LOGGER.error("Error while closing mapper : `{}`", e.getMessage());
                        throw new UncheckedIOException(e);
                    }
                });
    }

    //TODO : test if working

    /**
     * @param data  contains the data you want to create. The data must be in csv format.
     * @param clazz is the Object.class you want to create from the data. User.class or Group.class for example.
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
}
