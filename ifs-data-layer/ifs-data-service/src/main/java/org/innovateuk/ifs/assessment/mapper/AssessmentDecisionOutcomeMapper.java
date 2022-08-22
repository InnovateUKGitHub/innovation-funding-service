package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.assessment.domain.AssessmentDecisionOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentDecisionOutcomeResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Maps between domain and resource DTO for {@link AssessmentDecisionOutcome}.
 */
@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class AssessmentDecisionOutcomeMapper {

    public abstract AssessmentDecisionOutcomeResource mapToResource(AssessmentDecisionOutcome domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "assessment", ignore = true),
            @Mapping(target = "process", ignore = true)
    })
    public abstract AssessmentDecisionOutcome mapToDomain(AssessmentDecisionOutcomeResource resource);

}