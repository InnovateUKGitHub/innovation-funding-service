package org.innovateuk.ifs.application.forms.sections.yourorganisation.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

/**
 * TODO DW - document this class
 */
public interface YourOrganisationService {

    ServiceResult<Long> getTurnover(long applicationId, long competitionId);

    ServiceResult<Long> getHeadCount(long applicationId, long competitionId);

    ServiceResult<Boolean> getStateAidEligibility(long applicationId);

    ServiceResult<OrganisationSize> getOrganisationSize(long applicationId, long organisationId);

    ServiceResult<Void> updateTurnover(long applicationId, long competitionId, Long value);

    ServiceResult<Void> updateHeadCount(long applicationId, long competitionId, Long value);
}
