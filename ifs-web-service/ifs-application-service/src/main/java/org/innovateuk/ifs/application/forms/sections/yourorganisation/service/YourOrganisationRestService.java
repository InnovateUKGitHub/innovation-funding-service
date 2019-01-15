package org.innovateuk.ifs.application.forms.sections.yourorganisation.service;

import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

/**
 * TODO DW - document this class
 */
@Service
public class YourOrganisationRestService extends BaseRestService {

    private String baseUrl = "/application/%d/organisation/%d/finance";

    private CompetitionRestService competitionRestService;

    YourOrganisationRestService(CompetitionRestService competitionRestService) {
        this.competitionRestService = competitionRestService;
    }

    public ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationFinancesWithGrowthTable(
            long applicationId,
            long organisationId) {

        return getWithRestResult(format(baseUrl + "/with-growth-table", applicationId, organisationId),
                OrganisationFinancesWithGrowthTableResource.class).
                toServiceResult();
    }

    public ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationFinancesWithoutGrowthTable(
            long applicationId,
            long organisationId) {

        return getWithRestResult(format(baseUrl + "/without-growth-table", applicationId, organisationId),
                OrganisationFinancesWithoutGrowthTableResource.class).
                toServiceResult();
    }

    public ServiceResult<Void> updateOrganisationFinancesWithGrowthTable(
            long applicationId,
            long organisationId,
            OrganisationFinancesWithGrowthTableResource finances) {

        return postWithRestResult(format(baseUrl + "/with-growth-table", applicationId, organisationId), finances,
                Void.class).
                toServiceResult();
    }

    public ServiceResult<Void> updateOrganisationFinancesWithoutGrowthTable(
            long applicationId,
            long organisationId,
            OrganisationFinancesWithoutGrowthTableResource finances) {

        return postWithRestResult(format(baseUrl + "/without-growth-table", applicationId, organisationId), finances, Void.class).
                toServiceResult();
    }

    public ServiceResult<Boolean> isIncludingGrowthTable(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).
                andOnSuccessReturn(competition -> TRUE.equals(competition.getIncludeProjectGrowthTable())).
                toServiceResult();
    }

    public ServiceResult<Boolean> isShowStateAidAgreement(long applicationId, long organisationId) {
        String url = format(baseUrl + "/show-state-aid", applicationId, organisationId);
        return getWithRestResult(url, Boolean.class).toServiceResult();
    }
}
