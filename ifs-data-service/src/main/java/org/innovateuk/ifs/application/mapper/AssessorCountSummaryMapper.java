package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.AssessorStatistics;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(config = GlobalMapperConfig.class, unmappedTargetPolicy = WARN)
public abstract class AssessorCountSummaryMapper extends BaseMapper<AssessorStatistics, AssessorCountSummaryResource, Long> {

    @Override
    public abstract AssessorCountSummaryResource mapToResource(AssessorStatistics assessorStatistics);

    @Override
    public abstract AssessorStatistics mapToDomain(AssessorCountSummaryResource assessorCountSummaryResource);
}
