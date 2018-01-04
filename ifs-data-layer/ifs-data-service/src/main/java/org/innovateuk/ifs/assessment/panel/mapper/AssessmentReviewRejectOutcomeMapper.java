package org.innovateuk.ifs.assessment.panel.mapper;

import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReviewRejectOutcome;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Maps between domain and resource DTO for {@link AssessmentRejectOutcome}.
 */
@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class AssessmentReviewRejectOutcomeMapper extends BaseMapper<AssessmentReviewRejectOutcome, AssessmentReviewRejectOutcomeResource, Long> {

    @Override
    @Mappings({
            @Mapping(target = "reason", source = "rejectionComment"),
    })
    public abstract AssessmentReviewRejectOutcomeResource mapToResource(AssessmentReviewRejectOutcome domain);

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "rejectionComment", source = "reason"),
            @Mapping(target = "assessmentPanelApplicationInvite", ignore = true),
            @Mapping(target = "process", ignore = true),
    })
    public abstract AssessmentReviewRejectOutcome mapToDomain(AssessmentReviewRejectOutcomeResource resource);

}