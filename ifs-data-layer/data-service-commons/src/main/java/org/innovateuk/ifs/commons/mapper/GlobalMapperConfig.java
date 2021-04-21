package org.innovateuk.ifs.commons.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;

import static org.mapstruct.ReportingPolicy.WARN;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = WARN,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface GlobalMapperConfig {
}
