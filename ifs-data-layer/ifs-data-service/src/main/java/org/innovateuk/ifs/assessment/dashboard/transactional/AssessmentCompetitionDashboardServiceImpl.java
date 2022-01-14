package org.innovateuk.ifs.assessment.dashboard.transactional;

import com.google.common.collect.Streams;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.sort;

@Service
@Transactional(readOnly = true)
public class AssessmentCompetitionDashboardServiceImpl implements AssessmentCompetitionDashboardService {

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long userId, long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).get();
        String innovationLead = competition.getLeadTechnologist() == null ? "" : competition.getLeadTechnologist().getName();

        if(competition.isAlwaysOpen()) {
            Optional<AssessmentPeriod> assessmentPeriod = competition.getAssessmentPeriods().stream().filter(p -> p.isOpen()).sorted(Comparator.comparing(AssessmentPeriod::getId)).findFirst();
            if(assessmentPeriod.isPresent()) {
                return getAssessorCompetitionDashboardResource(userId, competitionId, assessmentPeriod.get().getId());
            }
        }

        List<ApplicationAssessmentResource> assessments = applicationAssessmentService.getApplicationAssessmentResource(userId, competitionId).getSuccess();

        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = new AssessorCompetitionDashboardResource(
                competitionId,
                competition.getName(),
                innovationLead,
                competition.isAlwaysOpen(),
                null,
                competition.getAssessorAcceptsDate(),
                competition.getAssessorDeadlineDate(),
                assessments);

        return serviceSuccess(assessorCompetitionDashboardResource);
    }

    @Override
    public ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long userId, long competitionId, long assessmentPeriodId) {
        Competition competition = competitionRepository.findById(competitionId).get();
        String innovationLead = competition.getLeadTechnologist() == null ? "" : competition.getLeadTechnologist().getName();
        AtomicLong batchIndex = new AtomicLong(1L);
        Map<AssessmentPeriod, Long> batchIndexes = sort(competition.getAssessmentPeriods(), Comparator.comparingLong(o -> o.getId())).stream().collect(Collectors.toMap(Function.identity(), x-> batchIndex.getAndIncrement()));

        Optional<AssessmentPeriod> assessmentPeriod = competition.getAssessmentPeriods().stream().filter(period -> period.getId() == assessmentPeriodId).findFirst();

        List<ApplicationAssessmentResource> assessments = applicationAssessmentService.getApplicationAssessmentResource(userId, competitionId, assessmentPeriodId).getSuccess();

        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = new AssessorCompetitionDashboardResource(
                competitionId,
                competition.getName(),
                innovationLead,
                competition.isAlwaysOpen(),
                assessmentPeriod.isPresent() ? batchIndexes.get(assessmentPeriod.get()) : null,
                assessmentPeriod.isPresent() ? competition.getAssessorAcceptsDate(assessmentPeriod.get()) : competition.getAssessorAcceptsDate(),
                assessmentPeriod.isPresent() ? competition.getAssessorDeadlineDate(assessmentPeriod.get()) : competition.getAssessorDeadlineDate(),
                assessments);

        return serviceSuccess(assessorCompetitionDashboardResource);
    }
}