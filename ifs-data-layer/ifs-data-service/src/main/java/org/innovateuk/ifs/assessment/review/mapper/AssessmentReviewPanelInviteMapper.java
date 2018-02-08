package org.innovateuk.ifs.assessment.review.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.competition.AssessmentReviewPanelInvite;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link AssessmentReviewPanelInvite} and {@link AssessmentReviewPanelInviteResource}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = { InnovationAreaMapper.class }
)
public abstract class AssessmentReviewPanelInviteMapper extends BaseMapper<AssessmentReviewPanelInvite, AssessmentReviewPanelInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.id", target = "competitionId"),
            @Mapping(source = "target.name", target = "competitionName"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "target.assessmentPanelDate", target = "panelDate"),
    })
    @Override
    public abstract AssessmentReviewPanelInviteResource mapToResource(AssessmentReviewPanelInvite domain);

    @Mappings({
            @Mapping(target="id", ignore=true),
            @Mapping(target="name", ignore=true),
            @Mapping(target="hash", ignore=true),
            @Mapping(target="user", ignore=true),
            @Mapping(target="target", ignore=true),
    })
    @Override
    public abstract AssessmentReviewPanelInvite mapToDomain(AssessmentReviewPanelInviteResource resource);

    public Long mapAssessmentPanelInviteToId(AssessmentReviewPanelInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
