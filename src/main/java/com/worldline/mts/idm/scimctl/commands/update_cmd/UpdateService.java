package com.worldline.mts.idm.scimctl.commands.update_cmd;

import com.worldline.mts.idm.scimctl.utils.RequestUtils;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import java.io.IOException;

@ApplicationScoped
@Named("UpdateService")
@Unremovable
public class UpdateService {

  @Inject
  RequestUtils requestUtils;

    public <T extends ResourceNode> void updateUser(String id, String resource, Class<T> clazz) throws IOException, BadRequestException {
      //requestUtils.createResource();
    }

}
