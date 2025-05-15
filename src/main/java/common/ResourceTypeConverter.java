package common;

import picocli.CommandLine;

public class ResourceTypeConverter implements CommandLine.ITypeConverter<ResourceType> {
    @Override
    public ResourceType convert(String value) {
        try {
            return ResourceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException("Invalid resource type must be 'USER' or 'GROUP' (case insensitive) : " + value);
        }
    }
}
