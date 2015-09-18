package com.worth.ifs.application.domain;

import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class ResponseTest {
    Response response;

    Long id;
    Question question;
    ProcessRole updatedBy;
    String value;
    LocalDateTime date;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        question = new Question();
        updatedBy = new ProcessRole();
        value  = "testResponseValue";
        date = LocalDateTime.now();
        Application application = new Application();

        response = new Response(id, date, value, updatedBy, question, application);
    }

    @Test
    public void questionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(response.getId(), id);
        Assert.assertEquals(response.getUpdateDate(), date);
        Assert.assertEquals(response.getValue(), value);
        Assert.assertEquals(response.getUpdatedBy(), updatedBy);
        Assert.assertEquals(response.getQuestion(), question);
    }
}