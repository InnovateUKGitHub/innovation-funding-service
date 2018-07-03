package org.innovateuk.ifs.review.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionParticipantRoleMapper;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.mapper.RejectionReasonMapper;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Maps between domain and resource DTO for {@link ReviewParticipant}.
 */
@Mapper(
    config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class,
                UserMapper.class,
                ReviewInviteMapper.class,
                RejectionReasonMapper.class,
                CompetitionParticipantRoleMapper.class,
                ParticipantStatusMapper.class,
        }
)
public abstract class ReviewParticipantMapper extends BaseMapper<ReviewParticipant, ReviewParticipantResource, Long> {

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
            @Mapping(target = "awaitingApplications", ignore = true),
    })
    @Override
    public abstract ReviewParticipantResource mapToResource(ReviewParticipant domain);

    @Mappings({
            @Mapping(source = "userId", target = "user"),
            @Mapping(source = "competitionId", target = "process")
    })
    @Override
    public abstract ReviewParticipant mapToDomain(ReviewParticipantResource resource);

    public Long mapAssessmentReviewPanelParticipantToId(ReviewParticipant object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
