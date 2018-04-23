package org.innovateuk.ifs.interview.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.invite.resource.InterviewInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link InterviewInvite} and {@link InterviewInviteResource}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = { InnovationAreaMapper.class }
)
public abstract class InterviewInviteMapper extends BaseMapper<InterviewInvite, InterviewInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.id", target = "competitionId"),
            @Mapping(source = "target.name", target = "competitionName"),
            @Mapping(source = "user.id", target = "userId"),
    })
    @Override
    public abstract InterviewInviteResource mapToResource(InterviewInvite domain);

    @Mappings({
            @Mapping(target="id", ignore=true),
            @Mapping(target="name", ignore=true),
            @Mapping(target="hash", ignore=true),
            @Mapping(target="user", ignore=true),
            @Mapping(target="target", ignore=true),
    })
    @Override
    public abstract InterviewInvite mapToDomain(InterviewInviteResource resource);

    public Long mapAssessmentInterviewPanelInviteToId(InterviewInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
