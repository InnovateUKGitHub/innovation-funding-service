package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.cofunder.domain.CompetitionForCofunding;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.AssessorDashboardState;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ServiceResult<Map<AssessorDashboardState, List<CofunderDashboardCompetitionResource>>> getCompetitionsForCofunding(long userId) {

        List<CompetitionForCofunding> participantCompetitions = cofunderAssignmentRepository.findCompetitionsForParticipant(userId);

        List<CofunderDashboardCompetitionResource> pending = new ArrayList();
        List<CofunderDashboardCompetitionResource> upcoming = new ArrayList();
        List<CofunderDashboardCompetitionResource> previous = new ArrayList<>();

        participantCompetitions.forEach(competition -> {
            switch (competition.getCompetitionStatus()) {
                case READY_TO_OPEN:
                case COMPETITION_SETUP:
                    break;
                case IN_ASSESSMENT:
                    pending.add(createCompetitionResource(competition));
                    break;
                case CLOSED:
                case OPEN:
                    upcoming.add(createCompetitionResource(competition));
                    break;
                case PREVIOUS:
                case PROJECT_SETUP:
                case ASSESSOR_FEEDBACK:
                case FUNDERS_PANEL:
                    if (competition.getAccepted() != 0) {
                        previous.add(createCompetitionResource(competition));
                    }
                    break;
            }
        });

        Map<AssessorDashboardState, List<CofunderDashboardCompetitionResource>> result = new HashMap<>();
        result.put(AssessorDashboardState.INFLIGHT, pending);
        result.put(AssessorDashboardState.UPCOMING, upcoming);
        result.put(AssessorDashboardState.PREVIOUS, previous);

        return serviceSuccess(result);
    }

    private CofunderDashboardCompetitionResource createCompetitionResource(CompetitionForCofunding competitionForCofunding) {
        CofunderDashboardCompetitionResource resource = new CofunderDashboardCompetitionResource();
        resource.setCompetitionId(competitionForCofunding.getCompetitionId());
        resource.setCompetitionName(competitionForCofunding.getCompetitionName());
        resource.setFundingType(competitionForCofunding.getFundingType());
        resource.setCofunderAcceptDate(competitionForCofunding.getCofunderAcceptDate());
        resource.setCofunderDeadlineDate(competitionForCofunding.getCofunderDeadline());
        resource.setSubmitted(competitionForCofunding.getAccepted() + competitionForCofunding.getRejected());
        resource.setPendingAssessments(competitionForCofunding.getAssigned());
        return resource;
    }

    @Override
    public ServiceResult<CofunderDashboardCompetitionResource> getApplicationsForCofunding(long userId, long competitionId) {
        return null;
    }
}
