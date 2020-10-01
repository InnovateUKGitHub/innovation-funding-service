package org.innovateuk.ifs.management.cofunders.security;

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
public class CofundersPermissionRules {

    @Autowired
    private CompetitionRestService competitionRestService;

    @PermissionRule(value = "COFUNDERS", description = "Only project finance or competition admin can see cofunders " +
            "if the competition is in the correct state.")
    public boolean assessment(CompetitionCompositeId competitionCompositeId, UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionCompositeId.id()).getSuccess();
        return isInternalAdmin(loggedInUser) &&
                competitionIsInOpenOrLater(competition) &&
                competitionIsBeforeAssessmentClosed(competition);
    }

    private boolean competitionIsInOpenOrLater(CompetitionResource competition) {
        return competition.getCompetitionStatus().isLaterThan(CompetitionStatus.READY_TO_OPEN);
    }

    private boolean competitionIsBeforeAssessmentClosed(CompetitionResource competition) {
        return !competition.getCompetitionStatus().isLaterThan(CompetitionStatus.IN_ASSESSMENT);

    }
}
