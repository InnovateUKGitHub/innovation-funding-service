package org.innovateuk.ifs.management.interview.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;

@PermissionRules
@Component
public class InterviewPermissionRules {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @PermissionRule(value = "INTERVIEW", description = "Only project finance or competition admin can see interview panels" +
            "if the competition is in the correct state.")
    public boolean interviewPanel(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isInternalAdmin(loggedInUser) &&
                competitionHasInterviewPanel(competition) &&
                !competitionIsInInformOrLater(competition);
    }

    @PermissionRule(value = "INTERVIEW_APPLICATIONS", description = "Only project finance or competition admin can " +
            "see interview panel applications if the competition is in the correct state.")
    public boolean interviewPanelApplications(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isInternalAdmin(loggedInUser) &&
                competitionHasInterviewPanel(competition) &&
                competitionIsInFundersPanel(competition);
    }

    private boolean competitionHasInterviewPanel(CompetitionResource competition) {
        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();
        return Boolean.TRUE.equals(competitionAssessmentConfigResource.getHasInterviewStage());
    }

    private boolean competitionIsInFundersPanel(CompetitionResource competition) {
        return competition.getCompetitionStatus().equals(CompetitionStatus.FUNDERS_PANEL);
    }

    private boolean competitionIsInInformOrLater(CompetitionResource competition) {
        return competition.getCompetitionStatus().isLaterThan(CompetitionStatus.FUNDERS_PANEL);
    }
}