package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.resource.Role.*;
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
    private QuestionStatusService questionStatusService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionStatusService sectionStatusService;

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Override
    @Transactional
    public ServiceResult<BigDecimal> updateApplicationProgress(final Long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn(application -> {
                    BigDecimal percentageProgress = calculateApplicationProgress(application);
                    application.setCompletion(percentageProgress);
                    return percentageProgress;
        });
    }

    @Override
    @Transactional
    public boolean applicationReadyForSubmit(final Long id) {
        return find(applicationRepository.findOne(id), notFoundError(Application.class, id)).andOnSuccess(application -> {
            BigDecimal progressPercentage = calculateApplicationProgress(application);

            return sectionStatusService.childSectionsAreCompleteForAllOrganisations(null, id, null)
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
        List<Section> sections = application.getCompetition().getSections();

        List<Question> questions = sections.stream()
                .flatMap(section -> section.getQuestions().stream())
                .filter(Question::isMarkAsCompletedEnabled)
                .collect(toList());

        List<ProcessRole> processRoles = application.getProcessRoles();

        Set<Organisation> organisations = processRoles.stream()
                .filter(p -> p.getRole() == LEADAPPLICANT
                        || p.getRole() == APPLICANT
                        || p.getRole() == COLLABORATOR)
                .map(processRole -> organisationRepository.findOne(processRole.getOrganisationId()))
                .collect(Collectors.toSet());

        Long countMultipleStatusQuestionsCompleted = organisations.stream()
                .mapToLong(org -> questions.stream()
                        .filter(Question::getMarkAsCompletedEnabled)
                        .filter(q -> q.hasMultipleStatuses() && questionStatusService.isMarkedAsComplete(q, application.getId(), org.getId()).getSuccess()).count())
                .sum();

        Long countSingleStatusQuestionsCompleted = questions.stream()
                .filter(Question::getMarkAsCompletedEnabled)
                .filter(q -> !q.hasMultipleStatuses() && questionStatusService.isMarkedAsComplete(q, application.getId(), 0L).getSuccess())
                .count();

        Long countCompleted = countMultipleStatusQuestionsCompleted + countSingleStatusQuestionsCompleted;

        Long totalMultipleStatusQuestions = questions.stream().filter(Question::hasMultipleStatuses).count() * organisations.size();
        Long totalSingleStatusQuestions = questions.stream().filter(q -> !q.hasMultipleStatuses()).count();

        Long totalQuestions = totalMultipleStatusQuestions + totalSingleStatusQuestions;

        return percentage(countCompleted, totalQuestions);
    }
}
