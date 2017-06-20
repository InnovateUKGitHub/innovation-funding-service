package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(config = GlobalMapperConfig.class, unmappedTargetPolicy = WARN)
public abstract class ApplicationCountSummaryMapper extends BaseMapper<ApplicationStatistics, ApplicationCountSummaryResource, Long> {

    @Override
    public abstract ApplicationCountSummaryResource mapToResource(ApplicationStatistics application);

    @Override
    public abstract ApplicationStatistics mapToDomain(ApplicationCountSummaryResource applicationCountSummaryResource);
}
