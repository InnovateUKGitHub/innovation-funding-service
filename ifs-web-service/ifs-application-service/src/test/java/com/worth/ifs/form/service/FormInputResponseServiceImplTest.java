package com.worth.ifs.form.service;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.form.resource.FormInputResponseResource;

/**
 * Testing FormInputResponseServiceImpl and its interactions with its mock rest service.
 */
public class FormInputResponseServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FormInputResponseService service = new FormInputResponseServiceImpl();

    @Mock
    private FormInputResponseRestService restServiceMock;

    @Test
    public void test_getByApplication() {

        List<FormInputResponseResource> formInputResponses = newFormInputResponseResource().build(3);
        when(restServiceMock.getResponsesByApplicationId(123L)).thenReturn(restSuccess(formInputResponses));

        List<FormInputResponseResource> response = service.getByApplication(123L);
        assertEquals(formInputResponses, response);
    }

    @Test
    public void test_mapResponsesToFormInputs() {

        List<FormInputResponseResource> formInputResponses = newFormInputResponseResource().
                withFormInputs(3L, 2L, 1L).
                build(3);

        Map<Long, FormInputResponseResource> response = service.mapFormInputResponsesToFormInput(formInputResponses);
        assertEquals(formInputResponses.get(0), response.get(3L));
        assertEquals(formInputResponses.get(1), response.get(2L));
        assertEquals(formInputResponses.get(2), response.get(1L));
    }

    @Test
    public void test_save() {

        when(restServiceMock.saveQuestionResponse(123L, 456L, 789L, "A new value", false)).
                thenReturn(restSuccess(asList("an error", "another error")));

        List<String> responses = service.save(123L, 456L, 789L, "A new value", false);
        assertEquals(asList("an error", "another error"), responses);
    }
}
