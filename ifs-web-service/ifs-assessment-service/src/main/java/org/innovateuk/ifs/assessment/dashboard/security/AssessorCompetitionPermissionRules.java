package org.innovateuk.ifs.assessment.dashboard.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isAssessor;

@PermissionRules
@Component
public class AssessorCompetitionPermissionRules {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private InterviewInviteRestService interviewInviteRestService;

    @PermissionRule(value = "ASSESSOR_COMPETITION", description = "Only assessors can see the competition" +
            " if its in assessment.")
    public boolean assessorCompetition(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isAssessor(loggedInUser) && !competition.getCompetitionStatus().isLaterThan(CompetitionStatus.IN_ASSESSMENT);
    }

    @PermissionRule(value = "ASSESSOR_INTERVIEW", description = "Only assessors can see the competition" +
            " if its in interview panel.")
    public boolean assessorInterviewPanel(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isAssessor(loggedInUser) && assessorAssignedToInterview(competitionCompositeId, loggedInUser);
    }

    private boolean assessorAssignedToInterview(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        List<InterviewParticipantResource> interviewPanelInvitesList = interviewInviteRestService.getAllInvitesByUser(loggedInUser.getId()).getSuccess();
        List<Long> interviewPanelAcceptedCompetitionIds = interviewPanelInvitesList.stream()
                .filter(InterviewParticipantResource::isAccepted)
                .map(invites -> invites.getCompetitionId())
                .collect(Collectors.toList());
        return interviewPanelAcceptedCompetitionIds.contains(competitionCompositeId.id());
    }
}
