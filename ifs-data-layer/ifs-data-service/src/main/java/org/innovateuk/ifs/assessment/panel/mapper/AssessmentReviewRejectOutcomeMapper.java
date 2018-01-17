package org.innovateuk.ifs.assessment.panel.mapper;

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

    @Mappings({
            @Mapping(source = "rejectReason", target = "reason"),
    })    public abstract AssessmentReviewRejectOutcomeResource mapToResource(AssessmentReviewRejectOutcome domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "assessmentPanelApplicationInvite", ignore = true),
            @Mapping(target = "process", ignore = true),
            @Mapping(target = "rejectReason", source = "reason"),
    })
    public abstract AssessmentReviewRejectOutcome mapToDomain(AssessmentReviewRejectOutcomeResource resource);

}