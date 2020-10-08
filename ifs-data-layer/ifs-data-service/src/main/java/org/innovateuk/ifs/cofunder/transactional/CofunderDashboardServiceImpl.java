package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.cofunder.domain.CompetitionForCofunding;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionActiveResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionPreviousResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        List<CompetitionForCofunding> participantCompetitions = cofunderAssignmentRepository.findCompetitionsForParticipant(userId);

        // update resource name
        List<CofunderDashboardCompetitionActiveResource> pending = new ArrayList();
        List<CofunderDashboardCompetitionUpcomingResource> upcoming = new ArrayList();
        List<CofunderDashboardCompetitionPreviousResource> previous = new ArrayList<>();

        participantCompetitions.forEach(competition -> {
            switch (competition.getCompetitionStatus()) {
                case READY_TO_OPEN:
                case COMPETITION_SETUP:
                    break;
                case IN_ASSESSMENT:
                    pending.add(createAwaitingResource(competition));
                    break;
                case CLOSED:
                case OPEN:
                    upcoming.add(createUpcomingResource(competition));
                    break;
                case PREVIOUS:
                case PROJECT_SETUP:
                case ASSESSOR_FEEDBACK:
                case FUNDERS_PANEL:
                    // update to submitted
                    if (competition.getAccepted() != 0) {
                        previous.add(createPreviousResource(competition));
                    }
                    break;
            }
        });

        return serviceSuccess(new CofunderDashboardCompetitionResource(pending, upcoming, previous));
    }

    // update awaiting
    private CofunderDashboardCompetitionActiveResource createAwaitingResource(CompetitionForCofunding competitionForCofunding) {
        return new CofunderDashboardCompetitionActiveResource(
                competitionForCofunding.getCompetitionId(),
                competitionForCofunding.getCompetitionName(),
                competitionForCofunding.getCofunderDeadline(),
                competitionForCofunding.getAssigned(),
                competitionForCofunding.getFundingType());
    }

    private CofunderDashboardCompetitionUpcomingResource createUpcomingResource(CompetitionForCofunding competitionForCofunding) {
        return new CofunderDashboardCompetitionUpcomingResource(
                competitionForCofunding.getCompetitionId(),
                competitionForCofunding.getCompetitionName(),
                competitionForCofunding.getCofunderAcceptDate(),
                competitionForCofunding.getCofunderDeadline(),
                competitionForCofunding.getAssigned(),
                competitionForCofunding.getFundingType());
    }

    // getAccepted needs to be assessment submitted.

    private CofunderDashboardCompetitionPreviousResource createPreviousResource(CompetitionForCofunding competitionForCofunding) {
        return new CofunderDashboardCompetitionPreviousResource(
                competitionForCofunding.getCompetitionId(),
                competitionForCofunding.getCompetitionName(),
                competitionForCofunding.getAccepted(),
                competitionForCofunding.getFundingType());
    }

    @Override
    public ServiceResult<CofunderDashboardCompetitionResource> getApplicationsForCofunding(long userId, long competitionId) {
        return null;
    }
}
