package create_command;

import cli.ClientConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import common.UserDeserializer;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.builder.BulkBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.constants.ResourceTypeNames;
import de.captaingoldfish.scim.sdk.common.constants.enums.HttpMethod;
import de.captaingoldfish.scim.sdk.common.exceptions.BadRequestException;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.resources.multicomplex.Member;
import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TO NOT DO : manages duplicate user pas à moi de le faire
@ApplicationScoped
public class CreateService {

    @Inject
    ClientConfig config;

    @Inject
    ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateService.class);

    /**
     * <p> and send a create request to the scim server </p>
     *
     * @param path to JSON file
     * @throws RuntimeException if error isn't specified in norm RFC7644
     * @throws IOException      if error while reading the file
     */
    public void createUser(String path) throws RuntimeException, IOException {
        var user = validateUser(path);
        if (user.size() == 1) {
            sendRequest(user.getFirst());
        } else if (user.size() > 1) {
            sendRequest(user);
        }
    }

    public void createGroup(String path) {
        validateGroup(path);
    }

    /**
     * Parse json data and validate it
     * if valid return a group
     *
     * @param path to json data
     */
    private Group validateGroup(String path) {
        return null;
    }

    /**
     * Parse json data and validate it
     * if valid return user
     *
     * @param path to  json data
     *
     *             JSON stream
     */
    private List<User> validateUser(String path) throws IOException {
        // TODO : var json = "{}";
        // var user = objectMapper.readValue(json, User.class);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        mapper.registerModule(module);
        var users = mapper.readValue(new File(path), new TypeReference<List<User>>() {
        });
        users.forEach(user -> {
            System.out.println(user.toPrettyString());
        });
        return users;
    }

    private void sendRequest(User user) throws BadRequestException {
        var scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig());
        ServerResponse<User> response = scimRequestBuilder.create(User.class, EndpointPaths.USERS).setResource(user).sendRequest();
        if (response.isSuccess()) {
            System.out.println("User created successfully");
        } else if (response.getErrorResponse() == null) {
            throw new RuntimeException("no response body, error not in RFC7644");
        } else {
            throw new BadRequestException("Error requesting the server : " + response.getErrorResponse());
        }
    }

    private void sendRequest(List<User> users) throws BadRequestException, RuntimeException {
        var scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig());
        var builder = scimRequestBuilder.bulk();
        List<Member> groupMembers = new ArrayList<>();

        for (User user : users) {
            String userBulkId = UUID.randomUUID().toString();

            if (user.getName().isEmpty()) {
                throw new RuntimeException("User must have a name for bulk request: " + user);
            }
            builder.bulkRequestOperation(EndpointPaths.USERS).method(HttpMethod.POST).data(user).bulkId(userBulkId).next();
            groupMembers.add(Member.builder().value("bulkId:" + userBulkId).type(ResourceTypeNames.USER).build());
        }
        Group finalGroup = Group.builder().displayName("finalGroup").members(groupMembers).build();

        ServerResponse<BulkResponse> response = builder
                .bulkRequestOperation(EndpointPaths.GROUPS)
                .method(HttpMethod.POST)
                .bulkId(UUID.randomUUID().toString())
                .data(finalGroup)
                .sendRequest();

        if (response.isSuccess()) {
            BulkResponse bulkResponse = response.getResource();
            LOGGER.info("Bulk Response: `{}`", bulkResponse);
            LOGGER.info("Failed Operations: `{}`", bulkResponse.getFailedOperations());
            LOGGER.info("Successful Operations: `{}`", bulkResponse.getSuccessfulOperations());
            LOGGER.info("HTTP Status: `{}`",bulkResponse.getHttpStatus());
        } else if (response.getErrorResponse() == null && response.getResource() == null) {
            throw new RuntimeException("no response body, error not in RFC7644 : " + response.getResponseBody());
        } else if (response.getErrorResponse() == null) {
            BulkResponse bulkResponse = response.getResource();
            throw new RuntimeException("bulk error : " + response.getResponseBody());
        } else {
            throw new BadRequestException("bad request : " + response.getErrorResponse());
        }
    }
}




