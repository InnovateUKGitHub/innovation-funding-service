package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.LibraryCandidate;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;

import static org.mapstruct.ReportingPolicy.WARN;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = WARN,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
@LibraryCandidate
public interface GlobalMapperConfig {
}
