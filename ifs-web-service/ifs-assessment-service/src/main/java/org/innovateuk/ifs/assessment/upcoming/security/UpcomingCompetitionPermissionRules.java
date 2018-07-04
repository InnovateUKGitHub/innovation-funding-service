package org.innovateuk.ifs.assessment.upcoming.security;

import org.innovateuk.ifs.assessment.service.CompetitionParticipantRestService;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isAssessor;

@PermissionRules
@Component
public class UpcomingCompetitionPermissionRules {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionParticipantRestService competitionParticipantRestService;

    @PermissionRule(value = "UPCOMING_COMPETITION", description = "Only assessors can see the upcoming competition" +
            " for which they are invited.")
    public boolean upcomingCompetition(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isAssessor(loggedInUser)
                && !competition.getCompetitionStatus().isLaterThan(CompetitionStatus.IN_ASSESSMENT)
                && assessorHasAcceptedInvite(competitionCompositeId, loggedInUser);
    }

    private boolean assessorHasAcceptedInvite(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {

        List<CompetitionParticipantResource> competitionParticipantList = competitionParticipantRestService.getParticipants(loggedInUser.getId(), CompetitionParticipantRoleResource.ASSESSOR).getSuccess();

        competitionParticipantList = competitionParticipantList.stream()
                .filter(participant -> participant.getCompetitionId().equals(competitionCompositeId.id()))
                .filter(participant -> participant.getStatus().equals(ParticipantStatusResource.ACCEPTED))
                .collect(toList());

        return !competitionParticipantList.isEmpty();
    }
}
