package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.question.transactional.template.DefaultApplicationQuestionCreator;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.innovateuk.ifs.question.transactional.template.QuestionTemplatePersistorImpl;
import org.innovateuk.ifs.setup.resource.QuestionSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_FORBIDDEN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.setup.resource.QuestionSection.APPLICATION_QUESTIONS;
import static org.innovateuk.ifs.setup.resource.QuestionSection.PROJECT_DETAILS;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


/**
 * Service that adds and deletes Questions to competitions.
 */
@Service
public class QuestionSetupTemplateServiceImpl implements QuestionSetupTemplateService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private DefaultApplicationQuestionCreator defaultApplicationQuestionCreator;

    @Autowired
    private QuestionTemplatePersistorImpl questionTemplatePersistorServiceImpl;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionPriorityOrderService questionPriorityService;

    @Override
    public ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition) {
        if (competition == null || competitionIsNotInSetupOrReadyToOpenState(competition)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        return find(sectionRepository.findFirstByCompetitionIdAndName(
                competition.getId(),
                APPLICATION_QUESTIONS.getName()),
                notFoundError(Section.class)
        ).andOnSuccess(section -> initializeAndPersistQuestion(section, competition));
    }

    @Override
    public ServiceResult<Void> deleteQuestionInCompetition(Long questionId) {
        return find(questionRepository.findFirstById(questionId),
                notFoundError(Question.class, questionId))
                .andOnSuccess(question -> deleteQuestion(question));
    }

    private ServiceResult<Void> deleteQuestion(Question question) {
        if (question.getCompetition() == null || competitionIsNotInSetupOrReadyToOpenState(question.getCompetition())) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (sectionIsInValidForDeletion(question.getSection().getName())) {
            return serviceFailure(new Error(GENERAL_FORBIDDEN));
        }

        if (questionRepository.countByCompetitionId(question.getCompetition().getId()) <= 1) {
            return serviceFailure(new Error(GENERAL_FORBIDDEN));
        }

        questionTemplatePersistorServiceImpl.deleteEntityById(question.getId());
        questionPriorityService.reprioritiseAssessedQuestionsAfterDeletion(question);

        return serviceSuccess();
    }

    private ServiceResult<Question> initializeAndPersistQuestion(Section applicationQuestionsSection, Competition competition) {
        Question question = defaultApplicationQuestionCreator.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);

        Question createdQuestion = questionTemplatePersistorServiceImpl.persistByEntity(Arrays.asList(question)).get(0);
        Question prioritizedQuestion = questionPriorityService.prioritiseAssessedQuestionAfterCreation(createdQuestion);

        return serviceSuccess(prioritizedQuestion);
    }

    private boolean competitionIsNotInSetupOrReadyToOpenState(Competition competition) {
        return !(competition.getCompetitionStatus().equals(COMPETITION_SETUP)
                || competition.getCompetitionStatus().equals(READY_TO_OPEN));
    }

    private boolean sectionIsInValidForDeletion(String sectionName) {
        return !sectionName.equals(APPLICATION_QUESTIONS.getName()) && !sectionName.equals(PROJECT_DETAILS.getName());
    }
}
