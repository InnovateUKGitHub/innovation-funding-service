package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * service for logic around granting terms and conditions of competitions in the setup phase.
 */
public interface CompetitionSetupGrantTermsAndConditionsService {

    ServiceResult<Void> updateTermsAndConditionsForCompetition(Long competitionId);
}
