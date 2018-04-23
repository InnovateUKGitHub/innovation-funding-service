package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.question.transactional.template.QuestionNumberOrderService;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.setup.resource.QuestionSection.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuestionNumberOrderServiceTest extends BaseServiceUnitTest<QuestionNumberOrderService> {

    public QuestionNumberOrderService supplyServiceUnderTest() {
        return new QuestionNumberOrderService();
    }

    @Test
    public void updateAssessedQuestionsNumbers() throws Exception {
        Competition competition = newCompetition().withId(1L).build();

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 4L, 3L)
                .withPriority(1, 2, 3, 4)
                .withQuestionNumber("1", "6", "3", null)
                .withCompetition(competition)
                .build(4);

        when(questionRepositoryMock.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competition.getId(), APPLICATION_QUESTIONS.getName()))
                .thenReturn(existingQuestions);

        service.updateAssessedQuestionsNumbers(competition.getId());

        List<Question> updatedAssessedQuestions = newQuestion()
                .withId(1L, 2L, 4L, 3L)
                .withPriority(1, 2, 3, 4)
                .withQuestionNumber("1", "2", "3", "4")
                .withCompetition(competition)
                .build(4);

        verify(questionRepositoryMock, times(1)).save(refEq(updatedAssessedQuestions));
    }

}