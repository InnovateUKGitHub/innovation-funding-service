package org.innovateuk.ifs.assessment.interview.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelInvite;
import org.innovateuk.ifs.invite.resource.AssessmentInterviewPanelInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link AssessmentInterviewPanelInvite} and {@link AssessmentInterviewPanelInviteResource}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = { InnovationAreaMapper.class }
)
public abstract class AssessmentInterviewPanelInviteMapper extends BaseMapper<AssessmentInterviewPanelInvite, AssessmentInterviewPanelInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.id", target = "competitionId"),
            @Mapping(source = "target.name", target = "competitionName"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "target.panelDate", target = "interviewDate"),
    })
    @Override
    public abstract AssessmentInterviewPanelInviteResource mapToResource(AssessmentInterviewPanelInvite domain);

    @Mappings({
            @Mapping(target="id", ignore=true),
            @Mapping(target="name", ignore=true),
            @Mapping(target="hash", ignore=true),
            @Mapping(target="user", ignore=true),
            @Mapping(target="target", ignore=true),
    })
    @Override
    public abstract AssessmentInterviewPanelInvite mapToDomain(AssessmentInterviewPanelInviteResource resource);

    public Long mapAssessmentInterviewPanelInviteToId(AssessmentInterviewPanelInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
