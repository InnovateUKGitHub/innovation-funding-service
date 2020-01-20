package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

/**
 * A rest service to support the Your organisation pages.
 */
@Service
public class ProjectYourOrganisationRestServiceImpl extends BaseRestService implements ProjectYourOrganisationRestService {

    private String baseUrl = "/project/%d/organisation/%d/finance";

    private CompetitionRestService competitionRestService;
    private ProjectRestService projectRestService;

    @Override
    public ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationFinancesWithGrowthTable(
            long projectId,
            long organisationId) {

        return getWithRestResult(format(baseUrl + "/with-growth-table", projectId, organisationId),
                OrganisationFinancesWithGrowthTableResource.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationFinancesWithoutGrowthTable(
            long projectId,
            long organisationId) {

        return getWithRestResult(format(baseUrl + "/without-growth-table", projectId, organisationId),
                OrganisationFinancesWithoutGrowthTableResource.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateOrganisationFinancesWithGrowthTable(
            long projectId,
            long organisationId,
            OrganisationFinancesWithGrowthTableResource finances) {

        return postWithRestResult(format(baseUrl + "/with-growth-table", projectId, organisationId), finances,
                Void.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateOrganisationFinancesWithoutGrowthTable(
            long projectId,
            long organisationId,
            OrganisationFinancesWithoutGrowthTableResource finances) {

        return postWithRestResult(format(baseUrl + "/without-growth-table", projectId, organisationId), finances, Void.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isShowStateAidAgreement(long projectId, long organisationId) {
        String url = format(baseUrl + "/show-state-aid", projectId, organisationId);
        return getWithRestResult(url, Boolean.class).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isIncludingGrowthTable(long projectId) {
        long competitionId = projectRestService.getProjectById(projectId).getSuccess().getCompetition();
        return competitionRestService.getCompetitionById(competitionId).
                andOnSuccessReturn(competition -> TRUE.equals(competition.getIncludeProjectGrowthTable())).
                toServiceResult();
    }
}
