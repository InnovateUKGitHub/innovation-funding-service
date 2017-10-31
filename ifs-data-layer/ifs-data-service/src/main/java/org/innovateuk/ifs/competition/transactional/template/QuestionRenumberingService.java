package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO: Add description
 */
@Service
public class QuestionRenumberingService {
    @Autowired
    private QuestionRepository questionRepository;

    private static final String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

    public void updateAssessedQuestionsNumbers(Long competitionId) {
        List<Question> assessedQuestions = questionRepository.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competitionId, ASSESSED_QUESTIONS_SECTION_NAME);

        Integer questionNumber = 1;

        for(Question question : assessedQuestions) {
            question.setQuestionNumber(questionNumber.toString());
            questionNumber++;
        }

        questionRepository.save(assessedQuestions);
    }
}
