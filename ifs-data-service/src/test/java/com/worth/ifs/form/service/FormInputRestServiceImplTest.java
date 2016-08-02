package com.worth.ifs.form.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResourceListType;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
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
        List<FormInputResource> expected = newFormInputResource()
                .build(2);

        Long questionId = 1L;
        FormInputScope scope = APPLICATION;

        setupGetWithRestResultExpectations(format("%s/findByQuestionId/%s/scope/%s", formInputRestUrl, questionId, scope), formInputResourceListType(), expected, OK);
        List<FormInputResource> response = service.getByQuestionIdAndScope(questionId, scope).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void testGetByCompetitionIdAndScope() throws Exception {
        List<FormInputResource> expected = newFormInputResource()
                .build(2);

        Long competitionId = 1L;
        FormInputScope scope = APPLICATION;

        setupGetWithRestResultExpectations(format("%s/findByCompetitionId/%s/scope/%s", formInputRestUrl, competitionId, scope), formInputResourceListType(), expected, OK);
        List<FormInputResource> response = service.getByCompetitionIdAndScope(competitionId, scope).getSuccessObject();
        assertSame(expected, response);
    }

}