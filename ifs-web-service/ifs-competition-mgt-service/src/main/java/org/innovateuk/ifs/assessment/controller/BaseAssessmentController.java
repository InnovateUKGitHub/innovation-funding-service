package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.application.resource.AssessmentCountSummaryPageResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;


// TODO not sure we really need the base class -- could combine into one controller

@SecuredBySpring(value = "Controller", description = "TODO", securedType = BaseAssessmentController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public abstract class BaseAssessmentController {

    protected static final int PAGE_SIZE = 20;

    @Autowired
    private CompetitionRestService competitionRestService;

    protected CompetitionResource getCompetition(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }
}