package org.innovateuk.ifs.question.transactional.template;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_FORBIDDEN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.COMPETITION_SETUP;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.genericQuestion;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


/**
 * Service that adds and deletes Questions to competitions.
 */
@Service
public class QuestionSetupAddAndRemoveServiceImpl implements QuestionSetupAddAndRemoveService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionPriorityOrderService questionPriorityService;

    @Autowired
    private QuestionNumberOrderService questionNumberOrderService;

    @Autowired
    private SetupStatusRepository setupStatusRepository;

    @Override
    public ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition) {
        //todo replace without template
        if (competition == null || competitionIsNotInSetupOrReadyToOpenState(competition)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        return find(sectionRepository.findByTypeAndCompetitionId(
                SectionType.APPLICATION_QUESTIONS,
                competition.getId()),
                notFoundError(Section.class)
        ).andOnSuccess(section -> initializeAndPersistQuestion(section, competition));
    }

    @Override
    public ServiceResult<Void> deleteQuestionInCompetition(long questionId) {
        return find(questionRepository.findFirstById(questionId),
                notFoundError(Question.class, questionId))
                .andOnSuccess(this::deleteQuestion);
    }

    private ServiceResult<Void> deleteQuestion(Question question) {
        if (question.getCompetition() == null || competitionIsNotInSetupOrReadyToOpenState(question.getCompetition())) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (!sectionIsValidForDeletion(question.getSection().getType())) {
            return serviceFailure(new Error(GENERAL_FORBIDDEN));
        }

        if (questionRepository.countByCompetitionId(question.getCompetition().getId()) <= 1) {
            return serviceFailure(new Error(GENERAL_FORBIDDEN));
        }

        setupStatusRepository.deleteByClassNameAndClassPk(Question.class.getName(), question.getId());
        questionRepository.deleteById(question.getId());
        questionPriorityService.reprioritiseQuestionsAfterDeletion(question);
        questionNumberOrderService.updateAssessedQuestionsNumbers(question.getCompetition().getId());
        return serviceSuccess();
    }

    private ServiceResult<Question> initializeAndPersistQuestion(Section applicationQuestionsSection, Competition competition) {

        Question createdQuestion = questionPriorityService.peristAndPrioritiesQuestions(competition, newArrayList(genericQuestion().build()), applicationQuestionsSection).get(0);
        Question prioritizedQuestion = questionPriorityService.prioritiseAssessedQuestionAfterCreation(createdQuestion);

        return serviceSuccess(prioritizedQuestion);
    }

    private boolean competitionIsNotInSetupOrReadyToOpenState(Competition competition) {
        return !(competition.getCompetitionStatus().equals(COMPETITION_SETUP)
                || competition.getCompetitionStatus().equals(READY_TO_OPEN));
    }

    private boolean sectionIsValidForDeletion(SectionType sectionType) {
        return sectionType == SectionType.PROJECT_DETAILS || sectionType == SectionType.APPLICATION_QUESTIONS;
    }
}
