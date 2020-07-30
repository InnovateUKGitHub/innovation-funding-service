package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;

/**
 * A rest service to support the Your organisation pages.
 */
public interface YourOrganisationRestService {

    ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationFinancesWithGrowthTable(
            long targetId,
            long organisationId);

    ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationFinancesWithoutGrowthTable(
            long targetId,
            long organisationId);

    ServiceResult<OrganisationFinancesKtpYearsResource> getOrganisationKtpYears(
            long targetId,
            long organisationId);

    ServiceResult<Void> updateOrganisationFinancesWithGrowthTable(
            long targetId,
            long organisationId,
            OrganisationFinancesWithGrowthTableResource finances);

    ServiceResult<Void> updateOrganisationFinancesWithoutGrowthTable(
            long targetId,
            long organisationId,
            OrganisationFinancesWithoutGrowthTableResource finances);

    ServiceResult<Void> updateOrganisationFinancesKtpYears(
            long targetId,
            long organisationId,
            OrganisationFinancesKtpYearsResource finances);

    ServiceResult<Boolean> isShowStateAidAgreement(long targetId, long organisationId);
}