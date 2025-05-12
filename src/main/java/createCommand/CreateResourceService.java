package createCommand;

import cli.ClientConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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

@ApplicationScoped
public class CreateResourceService {

    @Inject
    ClientConfig config;

    /**
     * <p> and send a create request to the scim server </p>
     * @param data JSON file
     * @throws RuntimeException if error isn't specified in norm RFC7644
     * @throws IOException if error while reading the file
     */
    //TODO : if many users send a bulk request
    public void createUser(File data) throws RuntimeException, IOException {
        var user = validateUser(data);
        String endpointPath = EndpointPaths.USERS;
        try (var scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig())) {
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
     * if valid return a group
     *
     * @param data json data
     */
    private Group validateGroup(File data) {
        return null;
    }

    /**
     * Parse json data and validate it
     * if valid return user
     *
     * @param data json data
     */
    //TODO : read multiple users in the file
    private User validateUser(File data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        mapper.registerModule(module);
        var user = mapper.readValue(new File(data.toURI()), User.class);
        //JsonNode jsonNode = mapper.readTree(new File(data.toURI()));

        System.out.println(user.toPrettyString());
        return user;
    }

}




