package GetRessource;

import cli.ClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;

import javax.swing.*;
import java.util.List;

@ApplicationScoped
@Named("resourceService")
@Unremovable
public class GetResourceService {

    @Inject
    ClientConfig config;

    public User getUserResource(String value) {
        var scimClientConfig = config.getScimClientConfig();
        try (ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), scimClientConfig)) {
            String endpointPath = EndpointPaths.USERS;
            ServerResponse<User> response = scimRequestBuilder.get(User.class, endpointPath, value).sendRequest();
            if (response.isSuccess()) {
                if (response.getResource() == null) {
                    throw new BadRequestException("User resource is null");
                }
                return response.getResource();
            } else if (response.getErrorResponse() == null) {
                // the response was not an error response as described in RFC7644
                throw new BadRequestException("no user found  :" + response.getResponseBody());
            } else {
                throw new BadRequestException("Id does not exist" + response.getResponseBody());
            }
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    public List<User> getUserResource() {
        List<User> users = null;
        String endpointPath = EndpointPaths.USERS;
        var scimClientConfig = config.getScimClientConfig();
        try (ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), scimClientConfig)) {
            var response = scimRequestBuilder.list(User.class, endpointPath).count(50).get().sendRequest();
            if(response.isSuccess()) {
                return response.getResource().getListedResources();
            } else if (response.getErrorResponse() == null) {
                // the response was not an error response as described in RFC7644
                throw new BadRequestException("no user found  :" + response.getResponseBody());
            } else {
                throw new BadRequestException(response.getResponseBody());
            }
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
        }
        return users;
    }
}
