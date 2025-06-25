package com.worldline.mts.idm.scimctl.options;

import org.eclipse.jetty.security.UserDataConstraint;

import de.captaingoldfish.scim.sdk.common.resources.Group;
import de.captaingoldfish.scim.sdk.common.resources.ResourceNode;
import io.vertx.ext.auth.User;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;

@ApplicationScoped
public class ResourceTypeConverter implements CommandLine.ITypeConverter<ResourceType> {

  @Override
  public ResourceType convert(String value) throws CommandLine.ParameterException {
    try {
      return valueToResourceType(value);
    } catch (IllegalArgumentException e) {
      throw new CommandLine.ParameterException(new CommandLine(this.getClass()), e.getMessage());
    }
  }

  private ResourceType valueToResourceType(String value) throws IllegalArgumentException {
    value = value.toLowerCase();
    if (value.equals("user")) {
      return ResourceType.USER;
    }
    if (value.equals("group")) {
      return ResourceType.GROUP;
    }
    System.err.println(value + "is not a type (user or group)");
    return null;
  }
}
