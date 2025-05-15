package utils;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.stream.Stream;

@Named("requesteUtils")
@Unremovable
@ApplicationScoped
public class RequestUtils {
    //TODO : get 1 resource
    public static <T> void getOneResource(Class<T> t, String id, Class<T> clazz) {
    }

    //TODO : bulk from 1 resource
    public static <T> void getResources(Class<T> t) {
    }

    //TODO : bulk from 1 resource + filter
    public static <T> void getResourcesWithFilter(Class<T> t, String... filters) {
    }

    //TODO : create 1 resource
    public static <T> void createResource(Class<T> t) {

    }

    //TODO : bulk create
    public static <T> void bulkCreate(Stream<T> t) {

    }

    //TODO : update 1 resource
    public static <T> void updateResource(String id, Class<T> t) {
    }

    //TODO : delete 1 resource
    public static <T> void deleteResource(String id) {
    }

    //TODO : bulk delete
    public static <T> void bulkDelete(Stream<String> ids) {

    }
}
