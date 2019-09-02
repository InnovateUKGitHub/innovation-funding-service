package org.innovateuk.ifs.commons.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;

import static org.mapstruct.ReportingPolicy.IGNORE;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface GlobalMapperConfig {
}
