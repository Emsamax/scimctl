package com.worldline.mts.idm.scimctl.commands.delete_cmd;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.response.ErrorResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@Named("deleteService")
@Unremovable
public class DeletService {

    @Inject
    ClientConfig config;

    public void deletUser(String id) {
        sendRequest(id);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DeletService.class);

    private void sendRequest(String id){
        var requestBuilder = new ScimRequestBuilder(config.getBaseUrl(), config.getScimClientConfig());
        ServerResponse<User> response = requestBuilder.delete(User.class, EndpointPaths.USERS, id)
                .sendRequest();
        if (response.isSuccess())
        {
            var resp  = response.getResource();
            LOGGER.info("User deleted successfully `{}`", resp);
        }
        else if(response.getErrorResponse() == null)
        {
            // the response was not an error response as described in RFC7644
            String errorMessage = response.getResponseBody();
            LOGGER.error("Error while deleting user: `{}`", errorMessage);
        }
        else
        {
            ErrorResponse errorResponse = response.getErrorResponse();
            LOGGER.error("Error `{}`", errorResponse);
        }
    }

}
