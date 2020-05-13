package org.innovateuk.ifs.management.competition.setup.core.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupInnovationLeadRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompetitionSetupInnovationLeadServiceImpl implements CompetitionSetupInnovationLeadService {

    @Autowired
    private CompetitionSetupInnovationLeadRestService competitionSetupInnovationLeadRestService;

    @Override
    public ServiceResult<Void> addInnovationLead(Long competitionId, Long innovationLeadUserId) {
        return competitionSetupInnovationLeadRestService.addInnovationLead(competitionId, innovationLeadUserId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeInnovationLead(Long competitionId, Long innovationLeadUserId) {
        return competitionSetupInnovationLeadRestService.removeInnovationLead(competitionId, innovationLeadUserId).toServiceResult();
    }
}
