package resource_type;

import picocli.CommandLine;

public class ResourceTypeConverter implements CommandLine.ITypeConverter<ResourceType> {
    @Override
    public ResourceType convert(String s) {
        try {
            return ResourceType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException("Invalid resource type must be 'USER' or 'GROUP' (case insensitive) : " + s);
        }
    }
}
