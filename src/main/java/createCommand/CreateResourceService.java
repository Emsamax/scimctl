package createCommand;

import cli.ClientConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TO NOT DO : manages duplicate user pas à moi de le faire
@ApplicationScoped
public class CreateResourceService {

    @Inject
    ClientConfig config;

    @Named


    /**
     * <p> and send a create request to the scim server </p>
     *
     * @param data JSON file
     * @throws RuntimeException if error isn't specified in norm RFC7644
     * @throws IOException      if error while reading the file
     */
    public void createUser(File data) throws RuntimeException, IOException {
        var user = validateUser(data);
        System.out.println("==============================" + user.size());
        if (user.size() == 1) {
            sendRequest(user.getFirst());
        } else {
            sendRequest(user);
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
    //TODO : read multiple users.json in the file
    private List<User> validateUser(File data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        mapper.registerModule(module);

        var users = mapper.readValue(new File(data.toURI()), new TypeReference<List<User>>() {
        });
        users.forEach(user -> {
            System.out.println(user.toPrettyString());
        });
        return users;
    }


    private void sendRequest(User user) {
        try (var scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig())) {
            ServerResponse<User> response = scimRequestBuilder.create(User.class, EndpointPaths.USERS).setResource(user).sendRequest();
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


    private void sendRequest(List<User> users) {
        try (var scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig())) {
            BulkBuilder builder = scimRequestBuilder.bulk();
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
                System.out.println("Bulk Response: " + bulkResponse);
                System.out.println("Failed Operations: " + bulkResponse.getFailedOperations());
                System.out.println("HTTP Status: " + bulkResponse.getHttpStatus());
            } else if (response.getErrorResponse() == null && response.getResource() == null) {
                throw new RuntimeException("no response body, error not in RFC7644 : " + response.getResponseBody());
            } else if (response.getErrorResponse() == null) {
                BulkResponse bulkResponse = response.getResource();
                throw new RuntimeException("bulk error : " + response.getResponseBody());
            } else {
                throw new BadRequestException("bad request : " + response.getErrorResponse());
            }
        } catch (BadRequestException e) {
            System.err.println("Error requesting the server : " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}




