package com.worldline.mts.idm.scimctl.commands.update_cmd;

import com.worldline.mts.idm.scimctl.config.ClientConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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

    //TODO mm chose que create mais besoin du créate
    public void updateUser(String id, String path) throws IOException, BadRequestException {
    }

}
