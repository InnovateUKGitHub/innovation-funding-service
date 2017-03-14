package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

/**
 * Testing FormInputResponseServiceImpl and its interactions with its mock rest service.
 */
public class FormInputResponseServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FormInputResponseService service = new FormInputResponseServiceImpl();

    @Mock
    private FormInputResponseRestService restServiceMock;

    @Test
    public void getByApplication() {

        List<FormInputResponseResource> formInputResponses = newFormInputResponseResource().build(3);
        when(restServiceMock.getResponsesByApplicationId(123L)).thenReturn(restSuccess(formInputResponses));

        List<FormInputResponseResource> response = service.getByApplication(123L);
        assertEquals(formInputResponses, response);
    }

    @Test
    public void mapResponsesToFormInputs() {

        List<FormInputResponseResource> formInputResponses = newFormInputResponseResource().
                withFormInputs(3L, 2L, 1L).
                build(3);

        Map<Long, FormInputResponseResource> response = service.mapFormInputResponsesToFormInput(formInputResponses);
        assertEquals(formInputResponses.get(0), response.get(3L));
        assertEquals(formInputResponses.get(1), response.get(2L));
        assertEquals(formInputResponses.get(2), response.get(1L));
    }

    @Test
    public void save() {

        ValidationMessages validation = new ValidationMessages(
                fieldError("value", "", "an error", NOT_ACCEPTABLE),
                fieldError("value", "", "another error", NOT_ACCEPTABLE));

        when(restServiceMock.saveQuestionResponse(123L, 456L, 789L, "A new value", false)).
                thenReturn(restSuccess(validation));

        ValidationMessages responses = service.save(123L, 456L, 789L, "A new value", false);
        assertEquals(validation, responses);
    }

    @Test
    public void getByApplicationIdAndQuestionName() throws Exception {
        long applicationId = 1L;
        String questionName = "name";

        FormInputResponseResource expected = newFormInputResponseResource().build();

        when(restServiceMock.getByApplicationIdAndQuestionName(applicationId, questionName)).thenReturn(
                restSuccess(expected));

        FormInputResponseResource actual = service.getByApplicationIdAndQuestionName(applicationId, questionName);
        assertEquals(expected, actual);

        verify(restServiceMock, only()).getByApplicationIdAndQuestionName(applicationId, questionName);
    }

    @Test
    public void getByApplicationIdAndQuestionId() {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(restServiceMock.getByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(
                restSuccess(expected));

        List<FormInputResponseResource> actual = service.getByApplicationIdAndQuestionId(applicationId, questionId);
        assertEquals(expected, actual);

        verify(restServiceMock, only()).getByApplicationIdAndQuestionId(applicationId, questionId);
    }
}
