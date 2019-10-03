package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
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
    @Transactional
    public boolean applicationReadyForSubmit(final long id) {
        return find(applicationRepository.findById(id), notFoundError(Application.class, id)).andOnSuccess(application -> {
            BigDecimal progressPercentage = calculateApplicationProgress(application);

            return sectionStatusService.sectionsCompleteForAllOrganisations(id)
                    .andOnSuccessReturn(allSectionsComplete -> {
                        Competition competition = application.getCompetition();
                        BigDecimal researchParticipation =
                                applicationFinanceHandler.getResearchParticipationPercentage(id);

                        boolean readyForSubmit = false;

                        if (allSectionsComplete
                                && progressPercentage.compareTo(BigDecimal.valueOf(100)) == 0
                                && researchParticipation.compareTo(BigDecimal.valueOf(competition.getMaxResearchRatio())) <= 0) {
                            readyForSubmit = true;
                        }

                        return readyForSubmit;
                    });
        }).getSuccess();
    }

    private BigDecimal calculateApplicationProgress(Application application) {
        long competitionId = application.getCompetition().getId();
        long organisations = organisationRepository.countDistinctByProcessRolesApplicationId(application.getId());
        long questionsWithMultipleStatuses = questionRepository.countByCompetitionIdAndMultipleStatusesAndMarkAsCompletedEnabledTrue(competitionId, true);
        long questionsWithSingleStatus = questionRepository.countByCompetitionIdAndMultipleStatusesAndMarkAsCompletedEnabledTrue(competitionId, false);
        long completedQuestionStatuses = questionStatusRepository.countByApplicationIdAndMarkedAsCompleteTrue(application.getId());
        long totalQuestions = questionsWithMultipleStatuses * organisations + questionsWithSingleStatus;
        return percentage(completedQuestionStatuses, totalQuestions);
    }
}
