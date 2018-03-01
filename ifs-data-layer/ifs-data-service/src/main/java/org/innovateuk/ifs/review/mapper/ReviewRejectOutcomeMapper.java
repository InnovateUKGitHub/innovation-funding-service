package org.innovateuk.ifs.review.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Maps between domain and resource DTO for {@link ReviewRejectOutcome}.
 */
@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class ReviewRejectOutcomeMapper {

    @Mappings({
            @Mapping(source = "rejectReason", target = "reason"),
    })    public abstract ReviewRejectOutcomeResource mapToResource(ReviewRejectOutcome domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "assessmentPanelApplicationInvite", ignore = true),
            @Mapping(target = "process", ignore = true),
            @Mapping(target = "rejectReason", source = "reason"),
    })
    public abstract ReviewRejectOutcome mapToDomain(ReviewRejectOutcomeResource resource);

}