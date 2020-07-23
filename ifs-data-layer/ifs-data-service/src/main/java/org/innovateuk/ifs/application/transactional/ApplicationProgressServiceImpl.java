package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.RELEASE_FEEDBACK;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MathFunctions.percentage;

/**
 * Service for retrieving and updating an {@Application}s progress percentage number.
 */
@Service
public class ApplicationProgressServiceImpl implements ApplicationProgressService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private SectionStatusService sectionStatusService;

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private ApplicationFinanceService applicationFinanceService;

    @Override
    @Transactional
    public ServiceResult<BigDecimal> updateApplicationProgress(final long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn(application -> {
                    BigDecimal percentageProgress = calculateApplicationProgress(application);
                    application.setCompletion(percentageProgress);
                    return percentageProgress;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean applicationReadyForSubmit(final long id) {
        return find(applicationRepository.findById(id), notFoundError(Application.class, id)).andOnSuccessReturn(application -> {
            BigDecimal progressPercentage = calculateApplicationProgress(application);
            BigDecimal researchParticipation = applicationFinanceHandler.getResearchParticipationPercentage(id);
            List<ApplicationFinanceResource> applicationFinanceResources = applicationFinanceHandler.getApplicationTotals(id);

//            is there a better way to do this
            BigDecimal totalFundingSought = applicationFinanceResources.stream()
                    .map(ApplicationFinanceResource::getTotalFundingSought)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);

            Competition competition = application.getCompetition();

//            If EOI or competition with no Finances
            if (RELEASE_FEEDBACK.equals(competition.getCompletionStage())) {
                return isApplicationPercentageComplete(progressPercentage, researchParticipation, competition);
            }

            return isCollaborativeFundingCriteriaMet(application)
                    && isFundingSoughtValid(competition, totalFundingSought)
                    && isApplicationPercentageComplete(progressPercentage, researchParticipation, competition);
        }).getSuccess();
    }

    private boolean isApplicationPercentageComplete(BigDecimal progressPercentage, BigDecimal researchParticipation, Competition competition) {
        return progressPercentage.compareTo(BigDecimal.valueOf(100)) == 0
                && researchParticipation.compareTo(BigDecimal.valueOf(competition.getMaxResearchRatio())) <= 0;
    }

    private boolean isCollaborativeFundingCriteriaMet(Application application) {
        if (application.isCollaborativeProject()) {
            return applicationFinanceService.collaborativeFundingCriteriaMet(application.getId()).getSuccess();
        }
        return true;
    }

    private boolean isFundingSoughtValid(Competition competition, BigDecimal totalFundingSought) {
        if (competition.getCompetitionApplicationConfig().getMaximumFundingSought() != null) {
            return totalFundingSought.compareTo(competition.getCompetitionApplicationConfig().getMaximumFundingSought()) <= 0;
        }
        return true;
    }


    private BigDecimal calculateApplicationProgress(Application application) {
        long competitionId = application.getCompetition().getId();
        long organisations = organisationRepository.countDistinctByProcessRolesApplicationId(application.getId());
        long questionsWithMultipleStatuses = questionRepository.countQuestionsWithMultipleStatuses(competitionId);
        long questionsWithSingleStatus = questionRepository.countQuestionsWithSingleStatus(competitionId);
        long completedQuestionStatuses = questionStatusRepository.countByApplicationIdAndMarkedAsCompleteTrue(application.getId());
        long totalQuestions = questionsWithMultipleStatuses * organisations + questionsWithSingleStatus;
        return percentage(completedQuestionStatuses, totalQuestions);
    }
}
