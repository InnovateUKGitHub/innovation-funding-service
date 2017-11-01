package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuestionNumberOrderServiceTest extends BaseServiceUnitTest<QuestionNumberOrderService> {

    private static String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

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

        when(questionRepositoryMock.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competition.getId(), ASSESSED_QUESTIONS_SECTION_NAME))
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