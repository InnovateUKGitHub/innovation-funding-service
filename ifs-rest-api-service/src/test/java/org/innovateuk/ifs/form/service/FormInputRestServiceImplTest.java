package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.formInputResourceListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
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
        List<FormInputResource> expected = Stream.of(1, 2, 3).map(i -> new FormInputResource()).collect(Collectors.toList());


        Long questionId = 1L;
        FormInputScope scope = FormInputScope.APPLICATION;

        setupGetWithRestResultExpectations(String.format("%s/find-by-question-id/%s/scope/%s", formInputRestUrl, questionId, scope), formInputResourceListType(), expected, OK);
        List<FormInputResource> response = service.getByQuestionIdAndScope(questionId, scope).getSuccess();
        assertSame(expected, response);
    }

    @Test
    public void testGetByCompetitionIdAndScope() throws Exception {
        List<FormInputResource> expected = Stream.of(1, 2, 3).map(i -> new FormInputResource()).collect(Collectors.toList());

        Long competitionId = 1L;
        FormInputScope scope = FormInputScope.APPLICATION;

        setupGetWithRestResultExpectations(String.format("%s/find-by-competition-id/%s/scope/%s", formInputRestUrl, competitionId, scope), formInputResourceListType(), expected, OK);
        List<FormInputResource> response = service.getByCompetitionIdAndScope(competitionId, scope).getSuccess();
        assertSame(expected, response);
    }

    @Test
    public void testDelete() throws Exception {
        Long formInputId = 1L;

        ResponseEntity<Void> result = setupDeleteWithRestResultExpectations(formInputRestUrl + "/" + formInputId);
        assertEquals(NO_CONTENT, result.getStatusCode());
    }

}
