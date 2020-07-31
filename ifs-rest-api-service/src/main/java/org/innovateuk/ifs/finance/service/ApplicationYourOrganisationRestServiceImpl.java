package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;



/**
 * A rest service to support the Your organisation pages.
 */
@Service
public class ApplicationYourOrganisationRestServiceImpl extends BaseRestService implements ApplicationYourOrganisationRestService {
    private String baseUrl = "/application/%d/organisation/%d/finance";

    @Override
    public ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationFinancesWithGrowthTable(
            long applicationId,
            long organisationId) {

        return getWithRestResult(format(baseUrl + "/with-growth-table", applicationId, organisationId),
                OrganisationFinancesWithGrowthTableResource.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationFinancesWithoutGrowthTable(
            long applicationId,
            long organisationId) {

        return getWithRestResult(format(baseUrl + "/without-growth-table", applicationId, organisationId),
                OrganisationFinancesWithoutGrowthTableResource.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<OrganisationFinancesKtpYearsResource> getOrganisationKtpYears(
            long applicationId,
            long organisationId) {

        return getWithRestResult(format(baseUrl + "/ktp-financial-years", applicationId, organisationId),
                OrganisationFinancesKtpYearsResource.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateOrganisationFinancesWithGrowthTable(
            long applicationId,
            long organisationId,
            OrganisationFinancesWithGrowthTableResource finances) {

        return postWithRestResult(format(baseUrl + "/with-growth-table", applicationId, organisationId), finances,
                Void.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateOrganisationFinancesWithoutGrowthTable(
            long applicationId,
            long organisationId,
            OrganisationFinancesWithoutGrowthTableResource finances) {

        return postWithRestResult(format(baseUrl + "/without-growth-table", applicationId, organisationId), finances, Void.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateOrganisationFinancesKtpYears(
            long applicationId,
            long organisationId,
            OrganisationFinancesKtpYearsResource finances) {

        return postWithRestResult(format(baseUrl + "/ktp-financial-years", applicationId, organisationId), finances, Void.class).
                toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isShowStateAidAgreement(long applicationId, long organisationId) {
        String url = format(baseUrl + "/show-state-aid", applicationId, organisationId);
        return getWithRestResult(url, Boolean.class).toServiceResult();
    }

}
