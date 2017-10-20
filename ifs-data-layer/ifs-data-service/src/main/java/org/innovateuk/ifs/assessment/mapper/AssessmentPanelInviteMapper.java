package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.AssessmentPanelInvite;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper between {@link AssessmentPanelInvite} and {@link CompetitionInviteResource}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = { InnovationAreaMapper.class }
)
public abstract class AssessmentPanelInviteMapper extends BaseMapper<AssessmentPanelInvite, CompetitionInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.id", target = "competitionId"),
            @Mapping(source = "target.name", target = "competitionName"),
            @Mapping(source = "target.assessorAcceptsDate", target = "acceptsDate"),
            @Mapping(source = "target.assessorDeadlineDate", target = "deadlineDate"),
            @Mapping(source = "target.assessorPay", target = "assessorPay"),
            @Mapping(source = "target.assessorBriefingDate", target = "briefingDate"),
            @Mapping(target="innovationArea", ignore=true),
    })
    @Override
    public abstract CompetitionInviteResource mapToResource(AssessmentPanelInvite domain);

    @Mappings({
            @Mapping(target="id", ignore=true),
            @Mapping(target="name", ignore=true),
            @Mapping(target="hash", ignore=true),
            @Mapping(target="user", ignore=true),
            @Mapping(target="target", ignore=true),
    })
    @Override
    public abstract AssessmentPanelInvite mapToDomain(CompetitionInviteResource resource);

    public Long mapCompetitionInviteToId(AssessmentPanelInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
