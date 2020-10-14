package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CofunderDashboardServiceImpl extends BaseTransactionalService implements CofunderDashboardService {

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CofunderAssignmentRepository cofunderAssignmentRepository;

    @Override
    public ServiceResult<CofunderDashboardCompetitionResource> getCompetitionsForCofunding(long userId) {
        return null;
    }

    @Override
    public ServiceResult<CofunderDashboardApplicationPageResource> getApplicationsForCofunding(long userId, long competitionId, Pageable pageable) {
        Page<CofunderDashboardApplicationResource> page =  cofunderAssignmentRepository.findApplicationsForCofunderCompetitionDashboard(userId, competitionId, pageable);
        return serviceSuccess(new CofunderDashboardApplicationPageResource(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent(),
                page.getNumber(),
                page.getSize()
        ));
    }
}
