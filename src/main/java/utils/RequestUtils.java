package utils;

import cli.ClientConfig;
import com.fasterxml.jackson.databind.JsonNode;
import common.ResourceType;
import de.captaingoldfish.scim.sdk.client.ScimRequestBuilder;
import de.captaingoldfish.scim.sdk.client.response.ServerResponse;
import de.captaingoldfish.scim.sdk.common.constants.EndpointPaths;
import de.captaingoldfish.scim.sdk.common.constants.ResourceTypeNames;
import de.captaingoldfish.scim.sdk.common.constants.enums.HttpMethod;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import de.captaingoldfish.scim.sdk.common.resources.multicomplex.Member;
import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Named("requestUtils")
@Unremovable
@ApplicationScoped
//TODO : custom split iterator pr opérations sur le stream pr bulk requests
public class RequestUtils {

    @Inject
    ClientConfig config;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    public <T> T getResourceRequest(String id, Class<T> clazz) throws BadRequestException, IllegalArgumentException, ClassCastException {
        var path = getEndPointPath(clazz);
        var scimClientConfig = config.getScimClientConfig();
        var scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), scimClientConfig);
        var response = scimRequestBuilder.get(User.class, path, id).sendRequest();

        if (response.isSuccess()) {
            if (response.getResource() == null) {
                throw new BadRequestException("User resource is null");
            }
            return castSucces(clazz, response);
        } else if (response.getErrorResponse() == null) {
            throw new BadRequestException(" response was not an error response as described in RFC7644  :" + response.getResponseBody());
        } else {
            throw new IllegalArgumentException("id does not exist" + response.getResponseBody());
        }
    }


    //TODO : list from 1 resource
    public <T> List<T> getResourcesRequest(Class<T> clazz) {
        /*
        if (!isUser(clazz) && !isGroup(clazz)) {
            throw new ClassCastException("Class is not User or Group : " + clazz.getName());
        }
        var path = getEndPointPath(clazz);
        var scimClientConfig = config.getScimClientConfig();
        var scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), scimClientConfig);


        if (isUser(clazz)) {
            Class<? extends User> userClazz = clazz.asSubclass(User.class);
        } else if (isGroup(clazz)) {
            Class<? extends Group> groupClazz = clazz.asSubclass(Group.class);
        }
        ServerResponse<ListResponse<ResourceNode>> response = scimRequestBuilder
                .list(clazz, path)
                .count(50)
                .get()
                .sendRequest();
            if (response.isSuccess()) {
                return (List<T>) response.getResource().getListedResources();
            }

            if (response.getErrorResponse() == null) {
                throw new BadRequestException("Invalid response format: " + response.getResponseBody());
            }
            throw new BadRequestException(response.getResponseBody());
            
         */
        return null;
    }

    //TODO : list from 1 resource + filter
    public <T> List<T> getFilteredResourcesRequest(Class<T> clazz, String... filters) {
        return null;
    }

    //TODO : create 1 resource
    public <T> void createResourceRequest(Class<T> clazz) {

    }

    //TODO : bulk create
    public <T> void createResourcesRequest(Stream<T> stream, Class<T> clazz) {
        String path = getEndPointPath(clazz);
        String resourceType = getResourceType(clazz);
        var scimRequestBuilder = new ScimRequestBuilder(config.getBaseUrl(), config.getScimClientConfig());
        var builder = scimRequestBuilder.bulk();
        var groupMembers = stream.map(resource -> (JsonNode) resource)
                .map(node -> {
                    if (node.get("userName").asText().isEmpty()) {
                        throw new RuntimeException("Resource must have a name for bulk request: " + node);
                    }
                    String userBulkId = UUID.randomUUID().toString();
                    builder.bulkRequestOperation(path)
                            .method(HttpMethod.POST)
                            .data(node)
                            .bulkId(userBulkId)
                            .next();
                    return Member.builder().value("bulkId:" + userBulkId).type(resourceType).build();
                }).toList(); // TODO : custom split iterator for cutting stream each 50 elements and send bulk request
        //TODO : not use .toList -> NO INTEREST
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
            LOGGER.info("HTTP Status: `{}`", bulkResponse.getHttpStatus());
        } else if (response.getErrorResponse() == null && response.getResource() == null) {
            throw new RuntimeException("no response body, error not in RFC7644 : " + response.getResponseBody());
        } else if (response.getErrorResponse() == null) {
            BulkResponse bulkResponse = response.getResource();
            throw new RuntimeException("bulk error : " + response.getResponseBody());
        } else {
            throw new de.captaingoldfish.scim.sdk.common.exceptions.BadRequestException("bad request : " + response.getErrorResponse());
        }

    }

    //TODO : update 1 resource
    public <T> void updateResourceRequest(String id, Class<T> t) {
    }

    //TODO : delete 1 resource
    public <T> void deleteResource(String id) {
    }

    //TODO : bulk delete
    public <T> void deleteResources(Stream<String> ids) {

    }

    private <T> String getEndPointPath(Class<T> clazz) throws ClassCastException {
        String endpointPath = "";
        if (clazz.equals(de.captaingoldfish.scim.sdk.common.resources.User.class)) {
            endpointPath = EndpointPaths.USERS;
        } else if (clazz.equals(de.captaingoldfish.scim.sdk.common.resources.Group.class)) {
            endpointPath = EndpointPaths.GROUPS;
        } else {
            throw new ClassCastException("Class is not User or Group : " + clazz.getName());
        }
        return endpointPath;
    }

    private <T> T castSucces(Class<T> clazz, ServerResponse<?> response) throws ClassCastException {
        var result = (T) response.getResource();
        if (result.getClass().isInstance(clazz)) {
            return result;
        } else {
            throw new ClassCastException("Resource is not of type " + clazz.getName());
        }
    }

    private <T> boolean isUser(Class<T> clazz) {
        return clazz.equals(User.class);
    }

    private <T> boolean isGroup(Class<T> clazz) {

        return clazz.equals(Group.class);
    }

    private <T> String getResourceType(Class<T> clazz) throws ClassCastException{
        if (isUser(clazz)) {
            return ResourceTypeNames.USER;
        } else if (isGroup(clazz)) {
            return ResourceTypeNames.GROUPS;
        } else {
            throw new ClassCastException("Class is not User or Group : " + clazz.getName());
        }

    }
}


