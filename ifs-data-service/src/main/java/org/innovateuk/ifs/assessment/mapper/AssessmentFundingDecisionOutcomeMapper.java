package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Maps between domain and resource DTO for {@link AssessmentFundingDecisionOutcome}.
 */
@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class AssessmentFundingDecisionOutcomeMapper {

    public abstract AssessmentFundingDecisionOutcomeResource mapToResource(AssessmentFundingDecisionOutcome domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "assessment", ignore = true),
            @Mapping(target = "process", ignore = true)
    })
    public abstract AssessmentFundingDecisionOutcome mapToDomain(AssessmentFundingDecisionOutcomeResource resource);

}