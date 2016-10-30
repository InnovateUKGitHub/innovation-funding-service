package com.worth.ifs.form.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.form.resource.FormInputResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResourceListType;

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
        List<FormInputResource> returnedFormInputs = Arrays.asList(1,2,3).stream().map(i -> new FormInputResource()).collect(Collectors.toList());

        setupGetWithRestResultExpectations(formInputRestURL + "/findByQuestionId/1", formInputResourceListType(), returnedFormInputs);

        List<FormInputResource> formInputs = service.getByQuestionId(1L).getSuccessObject();
        assertEquals(returnedFormInputs, formInputs);
    }

    @Test
    public void getOneTest() {
        FormInputResource formInputResource = new FormInputResource();
        setupGetWithRestResultExpectations(formInputRestURL + "/1", FormInputResource.class, formInputResource);

        FormInputResource returnedFormInputs = service.getById(1L).getSuccessObject();

        assertNotNull(returnedFormInputs);
        Assert.assertEquals(formInputResource, returnedFormInputs);
    }

    @Test
    public void findByCompetitionIdTest() {
        List<FormInputResource> formInputResources = Arrays.asList(1,2,3).stream().map(i -> new FormInputResource()).collect(Collectors.toList());;
        setupGetWithRestResultExpectations(formInputRestURL + "/findByCompetitionId/1", formInputResourceListType(), formInputResources);

        List<FormInputResource> returnedFormInputResources = service.getByCompetitionId(1L).getSuccessObject();

        assertNotNull(returnedFormInputResources);
        assertEquals(formInputResources, returnedFormInputResources);
    }
}
