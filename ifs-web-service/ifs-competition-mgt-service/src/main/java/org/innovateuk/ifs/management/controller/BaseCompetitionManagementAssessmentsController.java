package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.AssessmentCountSummaryPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;


// TODO not sure we really need the base class -- could combine into one controller
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public abstract class BaseCompetitionManagementAssessmentsController<T extends AssessmentCountSummaryPageResource> {

    protected static final int PAGE_SIZE = 20;

    @Autowired
    private CompetitionRestService competitionService;

    protected CompetitionResource getCompetition(long competitionId) {
        return competitionService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
    }
}