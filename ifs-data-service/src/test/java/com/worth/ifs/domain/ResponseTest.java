package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class ResponseTest {
    Response response;

    Long id;
    Question question;
    UserApplicationRole updatedBy;
    Boolean markedAsComplete;
    String value;
    LocalDateTime date;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        question = new Question();
        updatedBy = new UserApplicationRole();
        markedAsComplete = false;
        value  = "testResponseValue";
        date = LocalDateTime.now();
        Application application = new Application();

        response = new Response(id, date, value, markedAsComplete, updatedBy, question, application);
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(response.getId(), id);
        Assert.assertEquals(response.getUpdateDate(), date);
        Assert.assertEquals(response.getValue(), value);
        Assert.assertEquals(response.isMarkedAsComplete(), markedAsComplete);
        Assert.assertEquals(response.getUpdatedBy(), updatedBy);
        Assert.assertEquals(response.getQuestion(), question);
    }
}