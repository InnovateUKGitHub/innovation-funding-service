package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionTest {
    Question question;

    long id;
    Competition competition;
    Section section;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        competition = new Competition();
        section = new Section();
        name = "testQuestionName";

        question = new Question(id, competition, section, name);
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(question.getId(), id);
        Assert.assertEquals(question.getName(), name);
    }

}