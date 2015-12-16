package com.worth.ifs.form.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class FormInputResponseTest {
    private LocalDateTime updateDate = LocalDateTime.now();
    private String value = "";
    private ProcessRole updatedBy = null;
    private FormInput formInput = null;
    private Application application = null;
    private FormInputResponse formInputResponse;

    @Before
    public void setup(){
        initFormInputResponse();
    }

    public void initFormInputResponse(){
        formInputResponse = new FormInputResponse(updateDate, value, updatedBy, formInput, application);
    }



    @Test
    public void testGetWordCountEmptyValue() throws Exception {
        assertEquals(0L, formInputResponse.getWordCount().longValue());
    }

    @Test
    public void testGetWordCountSimpleText() throws Exception {
        value = "Wastage in our industry can be attributed in no small part to one issue. To date businesses have been reluctant to tackle that problem and instead worked around it. That has stifled progress.";
        initFormInputResponse();
        assertEquals(33L, formInputResponse.getWordCount().longValue());
    }

    /**
     * Test if the markdown text, is correctly stripped of the annotations.
     */
    @Test
    public void testGetWordCountMarkdownText() throws Exception {
        value = "Wastage in our industry can be attributed in no small part to one issue. To date businesses have been reluctant to tackle that problem and instead worked around it. That has stifled progress.\n" +
                "\n" +
                "1. **asadf**\n" +
                "2. **_asdf_**\n" +
                "3. _asdf_\n" +
                "4. sadf\n" +
                "\n" +
                "* **sadf**\n" +
                "* **_sdf_**\n" +
                "* _asdf_\n" +
                "* sdf\n" +
                "\n" +
                "The end result of our **_project_** will be a _novel_ tool to manage the **issue** and substantially reduce the wastage caused by it.";
        initFormInputResponse();
        assertEquals(64L, formInputResponse.getWordCount().longValue());
    }

    @Test
    public void testGetWordCountLeft() throws Exception {

    }
}