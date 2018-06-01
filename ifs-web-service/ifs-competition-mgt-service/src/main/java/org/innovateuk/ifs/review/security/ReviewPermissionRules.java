package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;

@PermissionRules
@Component
public class ReviewPermissionRules {

    @Autowired
    private CompetitionRestService competitionRestService;

    @PermissionRule(value = "REVIEW", description = "Only project finance or competition admin can see review panels" +
            "if the competition is in the correct state.")
    public boolean reviewPanel(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isInternalAdmin(loggedInUser) &&
                competitionHasReviewPanel(competition) &&
                !competitionIsInInformOrLater(competition);
    }

    @PermissionRule(value = "REVIEW_APPLICATIONS", description = "Only project finance or competition admin can " +
            "see review panel applications if the competition is in the correct state.")
    public boolean reviewPanelApplications(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isInternalAdmin(loggedInUser) &&
                competitionHasReviewPanel(competition) &&
                competitionIsInFundersPanel(competition);
    }

    private boolean competitionHasReviewPanel(CompetitionResource competition) {
        return competition.isHasAssessmentPanel();
    }

    private boolean competitionIsInFundersPanel(CompetitionResource competition) {
        return competition.getCompetitionStatus().equals(CompetitionStatus.FUNDERS_PANEL);
    }

    private boolean competitionIsInInformOrLater(CompetitionResource competition) {
        return competition.getCompetitionStatus().isLaterThan(CompetitionStatus.FUNDERS_PANEL);
    }
}