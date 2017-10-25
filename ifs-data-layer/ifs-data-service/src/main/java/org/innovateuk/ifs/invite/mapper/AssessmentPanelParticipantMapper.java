package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.assessment.mapper.AssessmentPanelInviteMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.invite.domain.AssessmentPanelParticipant;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class,
                UserMapper.class,
                AssessmentPanelInviteMapper.class,
                RejectionReasonMapper.class,
                CompetitionParticipantRoleMapper.class,
                ParticipantStatusMapper.class,
        }
)
public abstract class AssessmentPanelParticipantMapper extends BaseMapper<AssessmentPanelParticipant, CompetitionParticipantResource, Long> {

    @Mappings({
            @Mapping(source = "process.id", target = "competitionId"),
            @Mapping(source = "user", target = "userId"),
            @Mapping(source = "process.name", target = "competitionName"),
            @Mapping(source = "process.assessorAcceptsDate", target = "assessorAcceptsDate"),
            @Mapping(source = "process.assessorDeadlineDate", target = "assessorDeadlineDate"),
            @Mapping(target = "totalAssessments", ignore = true),
            @Mapping(target = "submittedAssessments", ignore = true),
            @Mapping(target = "pendingAssessments", ignore = true),
            @Mapping(source = "process.competitionStatus", target = "competitionStatus"),
    })
    @Override
    public abstract CompetitionParticipantResource mapToResource(AssessmentPanelParticipant domain);

    @Mappings({
            @Mapping(source = "userId", target = "user"),
            @Mapping(source = "competitionId", target = "process")
    })
    @Override
    public abstract AssessmentPanelParticipant mapToDomain(CompetitionParticipantResource resource);

    public Long mapCompetitionParticipantToId(AssessmentPanelParticipant object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
