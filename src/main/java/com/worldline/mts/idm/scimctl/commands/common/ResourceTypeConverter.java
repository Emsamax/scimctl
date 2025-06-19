package com.worldline.mts.idm.scimctl.commands.common;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;

@ApplicationScoped
public class ResourceTypeConverter implements CommandLine.ITypeConverter<FilterCommonOptions.ResourceType> {

  @Override
  public FilterCommonOptions.ResourceType convert(String value) throws CommandLine.ParameterException {
    try {
      return valueToResourceType(value);
    } catch (IllegalArgumentException e) {
      throw new CommandLine.ParameterException(new CommandLine(this.getClass()), e.getMessage());
    }
  }

  private FilterCommonOptions.ResourceType valueToResourceType(String value) throws IllegalArgumentException {
    value = value.toLowerCase();
    if (value.equals("user")) {
      return FilterCommonOptions.ResourceType.USER;
    }
    if (value.equals("group")) {
      return FilterCommonOptions.ResourceType.GROUP;
    }
    System.err.println(value + "is not a type (user or group)");
    throw new IllegalArgumentException(value + "is not a type (user or group)");
  }
}
