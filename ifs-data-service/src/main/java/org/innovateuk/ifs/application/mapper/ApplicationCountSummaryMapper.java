package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(config = GlobalMapperConfig.class, unmappedTargetPolicy = WARN)
public abstract class ApplicationCountSummaryMapper extends BaseMapper<Application, ApplicationCountSummaryResource, Long> {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "leadOrganisation.name", target = "leadOrganisation"),
            @Mapping(source = "assessors", target = "assessors"),
            @Mapping(source = "accepted", target = "accepted"),
            @Mapping(source = "submitted", target = "submitted")
    })
    @Override
    public abstract ApplicationCountSummaryResource mapToResource(Application application);

    @Override
    public abstract Application mapToDomain(ApplicationCountSummaryResource applicationCountSummaryResource);
}
