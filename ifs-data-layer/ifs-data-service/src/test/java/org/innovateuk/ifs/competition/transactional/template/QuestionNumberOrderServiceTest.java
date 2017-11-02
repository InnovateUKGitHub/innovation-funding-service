package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;

public class QuestionNumberOrderServiceTest extends BaseServiceUnitTest<QuestionNumberOrderService> {

    private static String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

    public QuestionNumberOrderService supplyServiceUnderTest() {
        return new QuestionNumberOrderService();
    }

    @Test
    public void updateAssessedQuestionsNumbers() throws Exception {
        Competition competition = newCompetition().withId(1L).build();

//        when(questionRepositoryMock.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competition.getId(), ASSESSED_QUESTIONS_SECTION_NAME, newlyCreatedQuestion.getPriority()));

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 3L, 4L)
                .withPriority(1, 2, 4, 3)
                .withQuestionNumber("1", "2", "3", null)
                .withCompetition(competition)
                .build(4);


    }

}