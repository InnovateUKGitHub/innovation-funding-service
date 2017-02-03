package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeResource;
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
public abstract class AssessmentRejectOutcomeMapper {

    public abstract AssessmentRejectOutcomeResource mapToResource(AssessmentRejectOutcome domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "assessment", ignore = true),
            @Mapping(target = "process", ignore = true),
    })
    public abstract AssessmentRejectOutcome mapToDomain(AssessmentRejectOutcomeResource resource);

}