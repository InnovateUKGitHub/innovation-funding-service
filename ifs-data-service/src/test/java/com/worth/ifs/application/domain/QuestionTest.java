package com.worth.ifs.application.domain;

import com.worth.ifs.competition.domain.Competition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuestionTest {
    Question question;

    Long id;
    Competition competition;
    Section section;
    String name;
    String number;
    String description;
    Integer priority;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        competition = newCompetition().build();
        name = "testQuestionName";
        number = "testQuestionNumber";
        description = "testQuestionDescription";
        priority = 1;

        question = newQuestion().
                with(id(id)).
                with(competition(competition)).
                with(name(name)).
                withQuestionNumber(number).
                with(description(description)).
                withPriority(priority).
                build();

        section = newSection().withQuestions(singletonList(question)).build();
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(question.getId(), id);
        Assert.assertEquals(question.getName(), name);
        Assert.assertEquals(question.getQuestionNumber(), number);
        Assert.assertEquals(question.getCompetition(), competition);
        Assert.assertEquals(question.getSection(), section);
        Assert.assertEquals(question.getDescription(), description);
        Assert.assertEquals(question.getPriority(), priority);
    }

    @Test
    public void equalsShouldWorkCorrect() throws Exception{
        Question question1 = newQuestion().
                with(id(id)).
                with(competition(competition)).
                with(name(name)).
                withQuestionNumber(number).
                with(description(description)).
                withPriority(priority).
                build();
        question1.setAssignEnabled(true);
        Question question2 = newQuestion().
                with(id(id)).
                with(competition(competition)).
                with(name(name)).
                withQuestionNumber(number).
                with(description(description)).
                withPriority(priority).
                build();
        question2.setAssignEnabled(true);

        assertTrue(question1.equals(question2));

        question1.setAssignEnabled(false);

        assertFalse(question.equals(question1));
        assertFalse(question.equals(new Section()));
        assertFalse(question.equals(null));

    }

    @Test
    public void hashCodeShouldWorkCorrectly() throws Exception{

    }
}