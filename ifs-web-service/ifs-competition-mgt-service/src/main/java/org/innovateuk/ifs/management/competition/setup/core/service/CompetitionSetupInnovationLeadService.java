package org.innovateuk.ifs.management.competition.setup.core.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

public interface CompetitionSetupInnovationLeadService {

    ServiceResult<Void> addInnovationLead(Long competitionId, Long innovationLeadUserId);

    ServiceResult<Void> removeInnovationLead(Long competitionId, Long innovationLeadUserId);
}
