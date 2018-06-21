package org.innovateuk.ifs.question.transactional.template;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.setup.resource.QuestionSection.APPLICATION_QUESTIONS;

/**
 * Service that can renumber questions by their set priority.
 */
@Service
public class QuestionNumberOrderService {
    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    @NotSecured("Must be secured by other services.")
    public void updateAssessedQuestionsNumbers(Long competitionId) {
        List<Question> assessedQuestions = questionRepository.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competitionId,
                APPLICATION_QUESTIONS.getName());

        Integer questionNumber = 1;

        for(Question question : assessedQuestions) {
            question.setQuestionNumber(questionNumber.toString());
            questionNumber++;
        }

        questionRepository.saveAll(assessedQuestions);
    }
}
