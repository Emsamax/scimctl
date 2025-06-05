package com.worldline.mts.idm.scimctl.commands.common;

import picocli.CommandLine;

public class ResourceTypeConverter implements CommandLine.ITypeConverter<FilterCommonOptions.ResourceType> {
    @Override
    public FilterCommonOptions.ResourceType convert(String value) {
        try {
            return FilterCommonOptions.ResourceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException("Invalid resource type must be 'USER' or 'GROUP' (case insensitive) : " + value);
        }
    }
}
