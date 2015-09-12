package com.worth.ifs.application.domain;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionType;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.domain.Competition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QuestionTest {
    Question question;

    Long id;
    Competition competition;
    Section section;
    String name;
    String description;
    String guidanceQuestion;
    String guidanceAnswer;
    Integer wordCount;
    String optionValues;
    List<Response> responses;
    QuestionType questionType;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        competition = new Competition();
        section = new Section();
        name = "testQuestionName";
        description = "testQuestionDescription";
        guidanceQuestion = "testGuidanceQuestion";
        guidanceAnswer = "testGuidanceAnswer";
        wordCount = 100;
        optionValues = "testOptionValues";
        responses = new ArrayList<Response>();
        responses.add(new Response());
        responses.add(new Response());
        responses.add(new Response());
        questionType = new QuestionType();


        question = new Question(optionValues, id, competition, section, questionType, responses, name, description, guidanceQuestion, guidanceAnswer, wordCount);
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(question.getId(), id);
        Assert.assertEquals(question.getName(), name);
        Assert.assertEquals(question.getCompetition(), competition);
        Assert.assertEquals(question.getSection(), section);
        Assert.assertEquals(question.getDescription(), description);
        Assert.assertEquals(question.getGuidanceQuestion(), guidanceQuestion);
        Assert.assertEquals(question.getGuidanceAnswer(), guidanceAnswer);
        Assert.assertEquals(question.getWordCount(), wordCount);
        Assert.assertEquals(question.getOptionValues(), optionValues);
        Assert.assertEquals(question.getResponses(), responses);
        Assert.assertEquals(question.getQuestionType(), questionType);


    }

}