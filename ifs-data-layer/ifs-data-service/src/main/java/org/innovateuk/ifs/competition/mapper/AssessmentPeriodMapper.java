package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                MilestoneMapper.class
        }
)
public abstract class AssessmentPeriodMapper extends BaseMapper<AssessmentPeriod, AssessmentPeriodResource, Long> {

    @Override
    public abstract AssessmentPeriodResource mapToResource(AssessmentPeriod domain);

    @Override
    public abstract AssessmentPeriod mapToDomain(AssessmentPeriodResource resource);

    public abstract List<AssessmentPeriod> mapToDomain(List<AssessmentPeriodResource> assessmentPeriodResources);

    public abstract List<AssessmentPeriodResource> mapToResource(List<AssessmentPeriod> assessmentPeriods);

    public Long mapAssessmentPeriodToId(AssessmentPeriod object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
