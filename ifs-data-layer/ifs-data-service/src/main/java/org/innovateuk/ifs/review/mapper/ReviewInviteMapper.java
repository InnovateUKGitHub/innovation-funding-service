package org.innovateuk.ifs.review.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link ReviewInvite} and {@link ReviewInviteResource}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = { InnovationAreaMapper.class }
)
public abstract class ReviewInviteMapper extends BaseMapper<ReviewInvite, ReviewInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.id", target = "competitionId"),
            @Mapping(source = "target.name", target = "competitionName"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "target.assessmentPanelDate", target = "panelDate"),
    })
    @Override
    public abstract ReviewInviteResource mapToResource(ReviewInvite domain);

    @Mappings({
            @Mapping(target="id", ignore=true),
            @Mapping(target="name", ignore=true),
            @Mapping(target="hash", ignore=true),
            @Mapping(target="user", ignore=true),
            @Mapping(target="target", ignore=true),
    })
    @Override
    public abstract ReviewInvite mapToDomain(ReviewInviteResource resource);

    public Long mapAssessmentPanelInviteToId(ReviewInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
