package com.worth.ifs.application.domain;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QuestionTypeTest {
    QuestionType questionType;

    Long id;
    String title;
    List<Question> questions;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        title = "questionTypeTitle";
        questions = new ArrayList<Question>();

        questions.add(new Question());
        questions.add(new Question());

        questionType = new QuestionType(id, title, questions);
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(questionType.getId(), id);
        Assert.assertEquals(questionType.getTitle(), title);
        Assert.assertEquals(questionType.getQuestions(), questions);
    }

}