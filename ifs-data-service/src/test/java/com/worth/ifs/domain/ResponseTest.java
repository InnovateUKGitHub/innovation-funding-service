package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class ResponseTest {
    Response response;

    Long id;
    Question question;
    UserApplicationRole userApplicationRole;
    Boolean markedAsComplete;
    String value;
    Date date;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        question = new Question();
        userApplicationRole = new UserApplicationRole();
        markedAsComplete = false;
        value  = "testResponseValue";
        date = new Date();
        Application application = new Application();

        response = new Response(id, date, value, markedAsComplete, userApplicationRole, question, application);
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(response.getId(), id);
        Assert.assertEquals(response.getDate(), date);
        Assert.assertEquals(response.getValue(), value);
        Assert.assertEquals(response.isMarkedAsComplete(), markedAsComplete);
        Assert.assertEquals(response.getUserApplicationRole(), userApplicationRole);
        Assert.assertEquals(response.getQuestion(), question);
    }
}