package common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.captaingoldfish.scim.sdk.common.resources.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Class for deserialize json data (group/user)
 */
public class JsonUtils<T> {
/**
    public static List<T> validateData(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        mapper.registerModule(module);
        var users = mapper.readValue(new File(path), new TypeReference<List<T>>() {
        });
        users.forEach(System.out::println);
        return users;
    }
 */
}
