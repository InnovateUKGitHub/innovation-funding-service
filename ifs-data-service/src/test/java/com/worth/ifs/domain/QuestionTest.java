package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class QuestionTest {
    Question question;

    long id;
    Competition competition;
    Section section;
    String name;
    String description;
    String helpTitle;
    String helpText;
    String questionGuidanceText;
    String answerGuidanceText;
    long characterCount;
    String values;
    List<Response> responses;
    QuestionType questionType;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        competition = new Competition();
        section = new Section();
        name = "testQuestionName";

        question = new Question(values, id, competition, section, questionType, responses, name, description, helpTitle, helpText, questionGuidanceText, answerGuidanceText, characterCount);
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(question.getId(), id);
        Assert.assertEquals(question.getName(), name);
    }

}