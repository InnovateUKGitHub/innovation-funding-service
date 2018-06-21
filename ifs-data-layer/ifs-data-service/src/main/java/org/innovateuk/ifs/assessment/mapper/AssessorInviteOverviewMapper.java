package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Mapper for mapping to a {@link AssessorInviteOverviewResource}.
 */
@Component
public class AssessorInviteOverviewMapper {

    private static final DateTimeFormatter detailsFormatter = ofPattern("d MMM yyyy");

    private ParticipantStatusMapper participantStatusMapper;
    private ProfileRepository profileRepository;
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    public AssessorInviteOverviewMapper(
            ParticipantStatusMapper participantStatusMapper,
            ProfileRepository profileRepository,
            InnovationAreaMapper innovationAreaMapper) {
        this.participantStatusMapper = participantStatusMapper;
        this.profileRepository = profileRepository;
        this.innovationAreaMapper = innovationAreaMapper;
    }

    public AssessorInviteOverviewResource mapToResource(AssessmentParticipant participant) {
        AssessorInviteOverviewResource assessorInviteOverviewResource = mapParticipantToResource(participant);

        if (participant.getUser() != null) {
            return mapUserToResource(participant.getUser(), assessorInviteOverviewResource);
        }

        assessorInviteOverviewResource.setInnovationAreas(singletonList(
                innovationAreaMapper.mapToResource(participant.getInvite().getInnovationArea())
        ));

        return assessorInviteOverviewResource;
    }

    public AssessorInviteOverviewResource mapToResource(CompetitionParticipant<?> participant) {
        AssessorInviteOverviewResource assessorInviteOverviewResource = mapParticipantToResource(participant);

        if (participant.getUser() != null) {
            return mapUserToResource(participant.getUser(), assessorInviteOverviewResource);
        }

        return assessorInviteOverviewResource;
    }

    private AssessorInviteOverviewResource mapParticipantToResource(CompetitionParticipant participant) {
        AssessorInviteOverviewResource assessorInviteOverviewResource = new AssessorInviteOverviewResource();
        assessorInviteOverviewResource.setName(participant.getInvite().getName());
        assessorInviteOverviewResource.setStatus(participantStatusMapper.mapToResource(participant.getStatus()));
        assessorInviteOverviewResource.setDetails(getDetails(participant));
        assessorInviteOverviewResource.setInviteId(participant.getInvite().getId());

        return assessorInviteOverviewResource;
    }

    private AssessorInviteOverviewResource mapUserToResource(
            User user,
            AssessorInviteOverviewResource assessorInviteOverviewResource) {

        Profile profile = profileRepository.findById(user.getProfileId()).get();

        assessorInviteOverviewResource.setId(user.getId());
        assessorInviteOverviewResource.setBusinessType(profile.getBusinessType());
        assessorInviteOverviewResource.setCompliant(profile.isCompliant(user));
        assessorInviteOverviewResource.setInnovationAreas(simpleMap(
                profile.getInnovationAreas(),
                innovationAreaMapper::mapToResource
        ));

        return assessorInviteOverviewResource;
    }

    private String getDetails(CompetitionParticipant<?> participant) {
        String details = null;

        if (participant.getStatus() == REJECTED) {
            RejectionReason rejectionReason = participant.getRejectionReason();
            if (rejectionReason != null) {
                details = format("Invite declined: %s", lowerCase(rejectionReason.getReason()));
            } else {
                details = format("Invite declined: %s", participant.getInvite().getSentOn().format(detailsFormatter));
            }
        } else if (participant.getStatus() == PENDING && participant.getInvite().getSentOn() != null) {
            details = format("Invite sent: %s", participant.getInvite().getSentOn().format(detailsFormatter));
        }

        return details;
    }
}
