package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;

/**
 * A rest service to support the Your organisation pages.
 */
public interface ProjectYourOrganisationRestService {

    ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationFinancesWithGrowthTable(
            long projectId,
            long organisationId);

    ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationFinancesWithoutGrowthTable(
            long projectId,
            long organisationId);

    ServiceResult<Void> updateOrganisationFinancesWithGrowthTable(
            long projectId,
            long organisationId,
            OrganisationFinancesWithGrowthTableResource finances);

    ServiceResult<Void> updateOrganisationFinancesWithoutGrowthTable(
            long projectId,
            long organisationId,
            OrganisationFinancesWithoutGrowthTableResource finances);

    ServiceResult<Boolean> isShowStateAidAgreement(long projectId, long organisationId);
}