package com.worth.ifs.commons.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface GlobalMapperConfig {
}
