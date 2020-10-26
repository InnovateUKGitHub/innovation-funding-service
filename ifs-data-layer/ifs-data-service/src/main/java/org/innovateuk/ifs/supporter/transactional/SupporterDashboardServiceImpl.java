package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.supporter.domain.CompetitionForCofunding;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.supporter.resource.*;
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
public class SupporterDashboardServiceImpl extends BaseTransactionalService implements SupporterDashboardService {

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Override
    public ServiceResult<Map<AssessorDashboardState, List<SupporterDashboardCompetitionResource>>> getCompetitionsForCofunding(long userId) {

        List<CompetitionForCofunding> participantCompetitions = supporterAssignmentRepository.findCompetitionsForParticipant(userId);

        List<SupporterDashboardCompetitionResource> pending = new ArrayList<>();
        List<SupporterDashboardCompetitionResource> previous = new ArrayList<>();

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

        Map<AssessorDashboardState, List<SupporterDashboardCompetitionResource>> result = new HashMap<>();
        result.put(AssessorDashboardState.INFLIGHT, pending);
        result.put(AssessorDashboardState.PREVIOUS, previous);

        return serviceSuccess(result);
    }

    private SupporterDashboardCompetitionResource createCompetitionResource(CompetitionForCofunding competitionForCofunding) {
        SupporterDashboardCompetitionResource resource = new SupporterDashboardCompetitionResource();
        resource.setCompetitionId(competitionForCofunding.getCompetitionId());
        resource.setCompetitionName(competitionForCofunding.getCompetitionName());
        resource.setFundingType(competitionForCofunding.getFundingType());
        resource.setSupporterAcceptDate(competitionForCofunding.getSupporterAcceptDate());
        resource.setSupporterDeadlineDate(competitionForCofunding.getSupporterDeadline());
        resource.setSubmitted(competitionForCofunding.getAccepted() + competitionForCofunding.getRejected());
        resource.setPendingAssessments(competitionForCofunding.getAssigned());
        return resource;
    }

    @Override
    public ServiceResult<SupporterDashboardApplicationPageResource> getApplicationsForCofunding(long userId, long competitionId, Pageable pageable) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> {
            CompetitionStatus status = competition.getCompetitionStatus();
            EnumSet<SupporterState> states = status.isLaterThan(CompetitionStatus.IN_ASSESSMENT)
                    ? EnumSet.of(SupporterState.ACCEPTED, SupporterState.REJECTED)
                    : EnumSet.allOf(SupporterState.class);
            Page<SupporterDashboardApplicationResource> page =  supporterAssignmentRepository.findApplicationsForSupporterCompetitionDashboard(userId, competitionId, states, pageable);
            return new SupporterDashboardApplicationPageResource(
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.getContent(),
                    page.getNumber(),
                    page.getSize()
            );
        });
    }
}
