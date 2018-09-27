package org.innovateuk.ifs.question.transactional.template;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.setup.resource.QuestionSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.setup.resource.QuestionSection.APPLICATION_QUESTIONS;

/**
 * Service that can reorder questions by priority after creation or deletion.
 */
@Service
public class QuestionPriorityOrderService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionNumberOrderService questionNumberOrderService;

    @Transactional
    @NotSecured("Must be secured by other services.")
    public Question prioritiseResearchCategoryQuestionAfterCreation(Question createdQuestion) {
        // The Research Category question should be prioritised after the Application Details question

        Question applicationDetailsQuestion = questionRepository.findFirstByCompetitionIdAndQuestionSetupType
                (createdQuestion.getCompetition().getId(), QuestionSetupType.APPLICATION_DETAILS);

        QuestionSection questionSection = QuestionSection.findByName(createdQuestion.getSection().getName());
        updateFollowingQuestionsPrioritiesByDelta(1, applicationDetailsQuestion.getPriority(), createdQuestion.getCompetition()
                .getId(), questionSection);

        createdQuestion.setPriority(applicationDetailsQuestion.getPriority() + 1);
        return questionRepository.save(createdQuestion);

    }

    @Transactional
    @NotSecured("Must be secured by other services.")
    public Question prioritiseAssessedQuestionAfterCreation(Question createdQuestion) {
        Question assessedQuestionWithHighestPriority = questionRepository
                .findFirstByCompetitionIdAndSectionNameOrderByPriorityDesc(createdQuestion.getCompetition().getId(),
                        APPLICATION_QUESTIONS.getName());
        createdQuestion.setPriority(assessedQuestionWithHighestPriority.getPriority() + 1);

        Question questionSaved = questionRepository.save(createdQuestion);

        questionNumberOrderService.updateAssessedQuestionsNumbers(createdQuestion.getCompetition().getId());

        return questionSaved;
    }

    @Transactional
    @NotSecured("Must be secured by other services.")
    public void reprioritiseQuestionsAfterDeletion(Question deletedQuestion) {
        QuestionSection questionSection = QuestionSection.findByName(deletedQuestion.getSection().getName());
        updateFollowingQuestionsPrioritiesByDelta(-1, deletedQuestion.getPriority(), deletedQuestion.getCompetition().getId(), questionSection);
    }

    private void updateFollowingQuestionsPrioritiesByDelta(int delta,
                                                           Integer priority,
                                                           long competitionId,
                                                           QuestionSection questionSection) {
        List<Question> subsequentQuestions = questionRepository
                .findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(competitionId,
                        questionSection.getName(), priority);

        subsequentQuestions.forEach(question -> question.setPriority(question.getPriority() + delta));

        questionRepository.save(subsequentQuestions);
    }
}
