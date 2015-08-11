package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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