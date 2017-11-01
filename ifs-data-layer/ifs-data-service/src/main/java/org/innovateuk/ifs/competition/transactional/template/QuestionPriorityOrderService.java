package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service that can reorder questions by priority after creation or deletion.
 */
@Service
public class QuestionPriorityOrderService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionNumberOrderService questionNumberOrderService;

    private static final String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

    @Transactional
    @NotSecured("Must be secured by other services.")
    public Question prioritiseAssessedQuestionAfterCreation(Question createdQuestion) {
        Question assessedQuestionWithHighestPriority = questionRepository.findFirstByCompetitionIdAndSectionNameOrderByPriorityDesc(createdQuestion.getCompetition().getId(), ASSESSED_QUESTIONS_SECTION_NAME);
        createdQuestion.setPriority(assessedQuestionWithHighestPriority.getPriority() + 1);

        questionNumberOrderService.updateAssessedQuestionsNumbers(createdQuestion.getCompetition().getId());

        return questionRepository.save(createdQuestion);
    }

    @Transactional
    @NotSecured("Must be secured by other services.")
    public void reprioritiseAssessedQuestionsAfterDeletion(Question deletedQuestion) {
        updateFollowingQuestionsPrioritiesByDelta(-1, deletedQuestion.getPriority(), deletedQuestion.getCompetition().getId());
        questionNumberOrderService.updateAssessedQuestionsNumbers(deletedQuestion.getCompetition().getId());
    }

    private void updateFollowingQuestionsPrioritiesByDelta(int delta, Integer priority, Long competitionId) {
        List<Question> subsequentQuestions = questionRepository.findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(competitionId, ASSESSED_QUESTIONS_SECTION_NAME, priority);

        subsequentQuestions.stream().forEach(question -> question.setPriority(question.getPriority() + delta));

        questionRepository.save(subsequentQuestions);
    }
}
