package updatecommand;

import cli.ClientConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import common.UserDeserializer;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.response.ErrorResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
@Named("UpdateService")
@Unremovable
public class UpdateService {

    @Inject
    ObjectMapper mapper;
    @Inject
    ClientConfig config;

    @Inject
    public UpdateService() {
    }

    private static Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);

    public void updateUser(String id, String path) throws IOException, BadRequestException {
        User user = validateData(path);
        sendRequest(id, user);
    }

    private User validateData(String path) throws IOException {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        mapper.registerModule(module);
        var user = mapper.readValue(new File(path), new TypeReference<List<User>>() {});
        System.out.println(user.getFirst().toPrettyString());
        return user.getFirst();
    }

    private void sendRequest(String id, User user) throws BadRequestException {
        var scimClientConfig = config.getScimClientConfig();
        ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), scimClientConfig);
        String endpointPath = EndpointPaths.USERS;
        ServerResponse<User> response = scimRequestBuilder.update(User.class, endpointPath, id)
                .setResource(user)
                .sendRequest();
        if (response.isSuccess())
        {
            User updatedUser = response.getResource();
            LOGGER.info("Updated user: `{}`", updatedUser.toPrettyString());
        }
        else if(response.getErrorResponse() == null)
        {
            // the response was not an error response as described in RFC7644
            String errorMessage = response.getResponseBody();
            LOGGER.error("Error while updating user: `{}`", errorMessage);
        }
        else
        {
            ErrorResponse errorResponse = response.getErrorResponse();
            LOGGER.error("Error `{}`", errorResponse);
        }
    }

}
