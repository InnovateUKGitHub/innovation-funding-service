package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionPriorityServiceImpl {
    @Autowired
    private QuestionRepository questionRepository;

    private static final String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

    @Transactional
    @NotSecured("Must be secured by other services.")
    public Question prioritiseAssessedQuestionAfterCreation(Question createdQuestion) {
        Question assessedQuestionWithHighestPriority = questionRepository.findFirstByCompetitionIdAndSectionNameOrderByPriorityDesc(createdQuestion.getCompetition().getId(), ASSESSED_QUESTIONS_SECTION_NAME);
        createdQuestion.setPriority(assessedQuestionWithHighestPriority.getPriority() + 1);

        updateFollowingQuestionsPrioritiesByDelta(1, createdQuestion.getPriority(), createdQuestion.getCompetition().getId());

        Question prioritisedQuestion = questionRepository.save(createdQuestion);
        updateAssessedQuestionsNumbers(prioritisedQuestion.getCompetition().getId());

        return prioritisedQuestion;
    }

    @Transactional
    @NotSecured("Must be secured by other services.")
    public void reprioritiseAssessedQuestionsAfterDeletion(Question deletedQuestion) {
        updateFollowingQuestionsPrioritiesByDelta(-1, deletedQuestion.getPriority(), deletedQuestion.getCompetition().getId());
        updateAssessedQuestionsNumbers(deletedQuestion.getCompetition().getId());
    }

    private void updateFollowingQuestionsPrioritiesByDelta(int delta, Integer priority, Long competitionId) {
        List<Question> subsequentQuestions = questionRepository.findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(competitionId, ASSESSED_QUESTIONS_SECTION_NAME, priority);

        subsequentQuestions.stream().forEach(question -> question.setPriority(question.getPriority() + delta));

        questionRepository.save(subsequentQuestions);
    }

    private void updateAssessedQuestionsNumbers(Long competitionId) {
        List<Question> assessedQuestions = questionRepository.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competitionId, ASSESSED_QUESTIONS_SECTION_NAME);

        Integer questionNumber = 1;

        for(Question question : assessedQuestions) {
            question.setQuestionNumber(questionNumber.toString());
            questionNumber++;
        }

        questionRepository.save(assessedQuestions);
    }
}
