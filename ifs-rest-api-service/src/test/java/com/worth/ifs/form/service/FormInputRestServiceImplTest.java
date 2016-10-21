package com.worth.ifs.form.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResourceListType;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class FormInputRestServiceImplTest extends BaseRestServiceUnitTest<FormInputRestServiceImpl> {

    private static String formInputRestUrl = "/forminput";

    @Override
    protected FormInputRestServiceImpl registerRestServiceUnderTest() {
        FormInputRestServiceImpl formInputRestService = new FormInputRestServiceImpl();
        formInputRestService.setFormInputRestURL(formInputRestUrl);
        return formInputRestService;
    }

    @Test
    public void testGetByQuestionIdAndScope() throws Exception {
        List<FormInputResource> expected = FormInputResourceBuilder.newFormInputResource()
                .build(2);

        Long questionId = 1L;
        FormInputScope scope = FormInputScope.APPLICATION;

        setupGetWithRestResultExpectations(String.format("%s/findByQuestionId/%s/scope/%s", formInputRestUrl, questionId, scope), ParameterizedTypeReferences.formInputResourceListType(), expected, OK);
        List<FormInputResource> response = service.getByQuestionIdAndScope(questionId, scope).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void testGetByCompetitionIdAndScope() throws Exception {
        List<FormInputResource> expected = FormInputResourceBuilder.newFormInputResource()
                .build(2);

        Long competitionId = 1L;
        FormInputScope scope = FormInputScope.APPLICATION;

        setupGetWithRestResultExpectations(String.format("%s/findByCompetitionId/%s/scope/%s", formInputRestUrl, competitionId, scope), ParameterizedTypeReferences.formInputResourceListType(), expected, OK);
        List<FormInputResource> response = service.getByCompetitionIdAndScope(competitionId, scope).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void testSave() throws Exception {
        FormInputResource expected = FormInputResourceBuilder.newFormInputResource().build();

        setupPutWithRestResultExpectations(formInputRestUrl + "/", FormInputResource.class, expected, expected);
        RestResult<FormInputResource> result = service.save(expected);
        assertTrue(result.isSuccess());
        Assert.assertEquals(expected, result.getSuccessObject());
    }

    @Test
    public void testDelete() throws Exception {
        Long formInputId = 1L;

        ResponseEntity<Void> result = setupDeleteWithRestResultExpectations(formInputRestUrl + "/" + formInputId);
        assertEquals(NO_CONTENT, result.getStatusCode());
    }

}