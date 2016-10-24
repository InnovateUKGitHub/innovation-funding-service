package com.worth.ifs.form.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.form.resource.FormInputResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResourceListType;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FormInputRestServiceMocksTest extends BaseRestServiceUnitTest<FormInputRestServiceImpl> {
    private static final String formInputRestURL = "/forminput";

    @Override
    protected FormInputRestServiceImpl registerRestServiceUnderTest() {
        FormInputRestServiceImpl formInputService = new FormInputRestServiceImpl();
        return formInputService;
    }

    @Test
    public void test_getFormInputByQuestionId() {
        List<FormInputResource> returnedFormInputs = newFormInputResource().build(3);

        setupGetWithRestResultExpectations(formInputRestURL + "/findByQuestionId/1", formInputResourceListType(), returnedFormInputs);

        List<FormInputResource> formInputs = service.getByQuestionId(1L).getSuccessObject();
        assertEquals(returnedFormInputs, formInputs);
    }

    @Test
    public void getOneTest() {
        FormInputResource formInputResource = newFormInputResource().build();
        setupGetWithRestResultExpectations(formInputRestURL + "/1", FormInputResource.class, formInputResource);

        FormInputResource returnedFormInputs = service.getById(1L).getSuccessObject();

        assertNotNull(returnedFormInputs);
        Assert.assertEquals(formInputResource, returnedFormInputs);
    }

    @Test
    public void findByCompetitionIdTest() {
        List<FormInputResource> formInputResources = newFormInputResource().build(3);
        setupGetWithRestResultExpectations(formInputRestURL + "/findByCompetitionId/1", formInputResourceListType(), formInputResources);

        List<FormInputResource> returnedFormInputResources = service.getByCompetitionId(1L).getSuccessObject();

        assertNotNull(returnedFormInputResources);
        assertEquals(formInputResources, returnedFormInputResources);
    }
}
