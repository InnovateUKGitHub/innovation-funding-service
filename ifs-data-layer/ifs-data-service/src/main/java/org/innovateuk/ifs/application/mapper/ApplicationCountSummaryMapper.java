package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ApplicationCountSummaryMapper extends BaseMapper<ApplicationStatistics, ApplicationCountSummaryResource, Long> {
    @Mappings({
            @Mapping(target = "leadOrganisation", ignore = true)
    })
    @Override
    public abstract ApplicationCountSummaryResource mapToResource(ApplicationStatistics application);

    @Mappings({
            @Mapping(target = "processRoles", ignore = true),
            @Mapping(target = "assessments", ignore = true)
    })
    @Override
    public abstract ApplicationStatistics mapToDomain(ApplicationCountSummaryResource applicationCountSummaryResource);
}
