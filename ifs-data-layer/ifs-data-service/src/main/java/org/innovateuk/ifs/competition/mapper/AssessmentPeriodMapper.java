package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class,
                MilestoneMapper.class
        }
)
public abstract class AssessmentPeriodMapper extends BaseMapper<AssessmentPeriod, AssessmentPeriodResource, Long> {

    @Mappings(
            @Mapping(source = "competition.id", target = "competitionId")
    )
    public abstract AssessmentPeriodResource mapToResource(AssessmentPeriod domain);

    @Mappings(
            @Mapping(source = "competitionId", target = "competition")
    )
    public abstract AssessmentPeriod mapToDomain(AssessmentPeriodResource resource);

    public Long mapAssessmentPeriodToId(AssessmentPeriod object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public AssessmentPeriod build() {
        return createDefault(AssessmentPeriod.class);
    }
}
