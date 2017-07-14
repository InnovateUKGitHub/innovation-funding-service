package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.AssessmentCountSummaryPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.innovateuk.ifs.util.MapFunctions.asMap;


// TODO not sure we really need the base class -- could combine into one controller
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public abstract class BaseCompetitionManagementAssessmentsController<T extends AssessmentCountSummaryPageResource> {

    protected static final int PAGE_SIZE = 20;

    @Autowired
    private CompetitionsRestService competitionService;

    protected CompetitionResource getCompetition(long competitionId) {
        return competitionService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
    }

    protected String buildBackUrl(String origin, long competitionId, MultiValueMap<String, String> queryParams) {
        String baseUrl = ApplicationOverviewOrigin.valueOf(origin).getBaseOriginUrl();
        queryParams.remove("origin");

        return UriComponentsBuilder.fromPath(baseUrl)
                .queryParams(queryParams)
                .buildAndExpand(asMap("competitionId", competitionId))
                .encode()
                .toUriString();
    }
}