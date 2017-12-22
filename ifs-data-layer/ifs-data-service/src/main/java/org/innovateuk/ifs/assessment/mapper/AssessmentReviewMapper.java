package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;

/**
 * Maps between domain and resource DTO for {@link Assessment}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProcessRoleMapper.class,
                ApplicationMapper.class,
                UserMapper.class,
                CompetitionMapper.class,
                AssessmentFundingDecisionOutcomeMapper.class,
                AssessmentRejectOutcomeMapper.class
        },
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class AssessmentReviewMapper extends BaseMapper<AssessmentReview, AssessmentReviewResource, Long> {

    @Mappings({
            @Mapping(source = "processEvent", target = "event"),
            @Mapping(source = "participant", target = "processRole"),
            @Mapping(source = "target", target = "application"),
            @Mapping(source = "target.name", target = "applicationName"),
            @Mapping(source = "target.competition", target = "competition"),
            @Mapping(source = "activityState", target = "assessmentReviewState")
    })
    @Override
    public abstract AssessmentReviewResource mapToResource(AssessmentReview domain);

    @Mappings({
            @Mapping(target = "processEvent", source = "event"),
            @Mapping(target = "participant", source = "processRole"),
            @Mapping(target = "target", source = "application"),
            @Mapping(target = "activityState", source = "assessmentReviewState", ignore = true),
            @Mapping(target = "responses", ignore = true),
            @Mapping(target = "processOutcomes", ignore = true),
            @Mapping(target = "lastModified", ignore = true)
    })
    @Override
    public abstract AssessmentReview mapToDomain(AssessmentReviewResource resource);

    public Long mapAssessmentToId(Assessment object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
