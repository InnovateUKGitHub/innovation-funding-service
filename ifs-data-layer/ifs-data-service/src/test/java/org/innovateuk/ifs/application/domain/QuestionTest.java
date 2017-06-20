package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static org.innovateuk.ifs.BuilderAmendFunctions.competition;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static java.util.Collections.singletonList;

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
}
