package createCommand;

import cli.ClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.exceptions.BadRequestException;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.resources.complex.Name;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class CreateResourceService {

    @Inject
    ClientConfig client;

    private static final String BASE_URL = "http://localhost:8080/base/scim/v2";

    public void createUser(File data) throws RuntimeException, IOException {
        var user = validateUser(data);
        String endpointPath = EndpointPaths.USERS;
        try (var scimRequestBuilder = new ScimRequestBuilder(BASE_URL, client.getScimClientConfig())) {
            ServerResponse<User> response = scimRequestBuilder.create(User.class, endpointPath).setResource(user).sendRequest();
            if (response.isSuccess()) {
                System.out.println("User created successfully");
            } else if (response.getErrorResponse() == null) {
                throw new RuntimeException("no response body, error not in RFC7644");
            } else {
                throw new BadRequestException("bad request : " + response.getErrorResponse());
            }
        } catch (BadRequestException e) {
            System.err.println("Error requesting the server : " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void createGroup(File data) {
        validateGroup(data);
    }

    /**
     * Parse json data and validate it
     * if valid return user
     *
     * @param data json data
     */
    private Group validateGroup(File data) {
        return null;
    }

    private User validateUser(File data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(new File(data.toURI()));
        User user = User.builder().build();
        jsonNode.fieldNames().forEachRemaining(entry -> {
            JsonNode value = jsonNode.get(entry);
            switch (entry) {
                //TODO : addapter au schema user
                case "userName" -> user.setUserName(value.asText());
                case "displayName" -> user.setDisplayName(value.asText());
                case "name" -> {
                    if (value.isObject()) {
                        var nameBuilder = new Name.NameBuilder();
                        if (value.has("formatted"))
                            nameBuilder.formatted(value.get("formatted").asText());
                        if (value.has("familyName"))
                            nameBuilder.familyName(value.get("familyName").asText());
                        if (value.has("givenName"))
                            nameBuilder.givenName(value.get("givenName").asText());
                        if (value.has("middleName"))
                            nameBuilder.middlename(value.get("middleName").asText());
                        if (value.has("honorificPrefix"))
                            nameBuilder.honorificPrefix(value.get("honorificPrefix").asText());
                        if (value.has("honorificSuffix"))
                            nameBuilder.honorificSuffix(value.get("honorificSuffix").asText());
                        user.setName(nameBuilder.build());
                    }
                }
            }
        });
        System.out.println(user.toPrettyString());
        return user;
    }

}




