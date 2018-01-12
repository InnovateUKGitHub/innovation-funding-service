package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.assessment.panel.domain.AssessmentReviewRejectOutcome;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Maps between domain and resource DTO for {@link org.innovateuk.ifs.assessment.panel.domain.AssessmentReviewRejectOutcome}.
 */
@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class AssessmentReviewRejectOutcomeMapper {

    public abstract AssessmentReviewRejectOutcomeResource mapToResource(AssessmentReviewRejectOutcome domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "assessmentPanelApplicationInvite", ignore = true),
            @Mapping(target = "process", ignore = true),
    })
    public abstract AssessmentReviewRejectOutcome mapToDomain(AssessmentReviewRejectOutcomeResource resource);

}