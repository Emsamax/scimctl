package GetRessource;

import cli.ClientConfig;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.constants.enums.Comparator;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.response.ListResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
@Named("resourceService")
@Unremovable
public class GetResourceService {

    @Inject
    ClientConfig config;

    public User getUserWithId(String id) throws BadRequestException, IllegalArgumentException {
        var scimClientConfig = config.getScimClientConfig();
        ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), scimClientConfig);
        String endpointPath = EndpointPaths.USERS;
        ServerResponse<User> response = scimRequestBuilder.get(User.class, endpointPath, id).sendRequest();
        if (response.isSuccess()) {
            if (response.getResource() == null) {
                throw new BadRequestException("User resource is null");
            }
            return response.getResource();
        } else if (response.getErrorResponse() == null) {
            // the response was not an error response as described in RFC7644
            throw new BadRequestException("no user found  :" + response.getResponseBody());
        } else {
            throw new IllegalArgumentException("id does not exist" + response.getResponseBody());
        }
    }


    public List<User> getUsers() throws BadRequestException {
        String endpointPath = EndpointPaths.USERS;
        var scimClientConfig = config.getScimClientConfig();
        ScimRequestBuilder scimRequestBuilder = new ScimRequestBuilder(config.getBASE_URL(), scimClientConfig);
        var response = scimRequestBuilder.list(User.class, endpointPath).count(50).get().sendRequest();
        if (response.isSuccess()) {
            return response.getResource().getListedResources();
        } else if (response.getErrorResponse() == null) {
            // the response was not an error response as described in RFC7644
            throw new BadRequestException("no user found  :" + response.getResponseBody());
        } else {
            throw new BadRequestException(response.getResponseBody());
        }
    }

    public List<User> getUserWithName(String name) throws BadRequestException {
        System.out.println(name);
        var requestBuilder = new ScimRequestBuilder(config.getBASE_URL(), config.getScimClientConfig());
        ServerResponse<ListResponse<User>> response = requestBuilder.list(User.class, EndpointPaths.USERS)
                .count(50)
                .filter("userName", Comparator.CO, name)
                .build()
                .post()
                .sendRequest();
        if (response.isSuccess()) {
            return response.getResource().getListedResources();
        } else if (response.getErrorResponse() == null) {
            throw new BadRequestException("no user found  :" + response.getResponseBody());
        } else {
            throw new BadRequestException(response.getResponseBody());
        }
    }

    //TODO
    public Group getGroupWithName(String name) {
        return null;
    }

    public List<Group> getGroups() {
        return null;
    }
}
