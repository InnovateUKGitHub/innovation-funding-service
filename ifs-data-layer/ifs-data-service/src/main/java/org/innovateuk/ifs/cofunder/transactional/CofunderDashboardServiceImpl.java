package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionActiveResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
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

        // finds competitions assigned to
        List<CofunderAssignment> assignments = cofunderAssignmentRepository.findByParticipantId(userId);

        Map<Competition, List<CofunderAssignment>> competitionAssignments =
                assignments.stream().collect(groupingBy(co -> co.getTarget().getCompetition()));

        List<CofunderDashboardCompetitionActiveResource> pending = new ArrayList();
        List<CofunderDashboardCompetitionUpcomingResource> upcoming = new ArrayList();

        competitionAssignments.keySet().forEach(competition -> {
            switch (competition.getCompetitionStatus()) {
                case READY_TO_OPEN:
                case PREVIOUS:
                case PROJECT_SETUP:
                case ASSESSOR_FEEDBACK:
                case FUNDERS_PANEL:
                case COMPETITION_SETUP:
                    break;
                case IN_ASSESSMENT:
                    pending.add(createAwaitingResource(competitionAssignments.get(competition), competition));
                    break;
                case CLOSED:
                    // do we want it to appear for open?
                case OPEN:
                    upcoming.add(createUpcomingResource(competitionAssignments.get(competition), competition));
            }
        });

        return serviceSuccess(new CofunderDashboardCompetitionResource(pending, upcoming));
    }

    // TODO: add count repo calls - replace the below loops

    private CofunderDashboardCompetitionActiveResource createAwaitingResource(List<CofunderAssignment> cofunderAssignments,
                                                                              Competition competition) {
        long pendingReview = cofunderAssignments.stream().filter(cofunderAssignment -> cofunderAssignment.getProcessState() ==  CofunderState.CREATED).count();

        return new CofunderDashboardCompetitionActiveResource(
                competition.getId(),
                competition.getName(),
                competition.getAssessorDeadlineDate(),
                pendingReview,
                competition.getFundingType(),
                competition.getDaysLeft());
    }

    private CofunderDashboardCompetitionUpcomingResource createUpcomingResource(List<CofunderAssignment> cofunderAssignments,
                                                                                Competition competition) {

        long upcomingReview = cofunderAssignments.stream().filter(cofunderAssignment -> cofunderAssignment.getProcessState() ==  CofunderState.CREATED).count();

        return new CofunderDashboardCompetitionUpcomingResource(
                competition.getId(),
                competition.getName(),
                competition.getAssessorAcceptsDate(),
                competition.getAssessorDeadlineDate(),
                upcomingReview,
                competition.getFundingType());
    }

    @Override
    public ServiceResult<CofunderDashboardCompetitionResource> getApplicationsForCofunding(long userId, long competitionId) {
        return null;
    }
}
