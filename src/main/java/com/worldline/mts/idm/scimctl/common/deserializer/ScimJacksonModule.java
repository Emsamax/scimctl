package com.worldline.mts.idm.scimctl.common.deserializer;


import com.fasterxml.jackson.databind.module.SimpleModule;
import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Unremovable
public class ScimJacksonModule extends SimpleModule {

  public ScimJacksonModule() {
    super("ScimJacksonModule");
    addDeserializer(User.class, new ResourceNodeDeserializer<>(User.class));
    addDeserializer(Group.class, new ResourceNodeDeserializer<>(Group.class));
  }
}
