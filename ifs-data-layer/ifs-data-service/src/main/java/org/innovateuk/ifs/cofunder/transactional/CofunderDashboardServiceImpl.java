package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.cofunder.domain.CompetitionForCofunding;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

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

        List<CofunderDashboardCompetitionResource> pending = new ArrayList<>();
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
    public ServiceResult<CofunderDashboardApplicationPageResource> getApplicationsForCofunding(long userId, long competitionId, Pageable pageable) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> {
            CompetitionStatus status = competition.getCompetitionStatus();
            EnumSet<CofunderState> states = status.isLaterThan(CompetitionStatus.IN_ASSESSMENT)
                    ? EnumSet.of(CofunderState.ACCEPTED, CofunderState.REJECTED)
                    : EnumSet.allOf(CofunderState.class);
            Page<CofunderDashboardApplicationResource> page =  cofunderAssignmentRepository.findApplicationsForCofunderCompetitionDashboard(userId, competitionId, states, pageable);
            return new CofunderDashboardApplicationPageResource(
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.getContent(),
                    page.getNumber(),
                    page.getSize()
            );
        });
    }
}
