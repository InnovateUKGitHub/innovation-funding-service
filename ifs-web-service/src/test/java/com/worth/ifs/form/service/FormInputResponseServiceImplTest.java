package com.worth.ifs.form.service;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.util.CollectionFunctions.reverse;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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

        List<FormInputResponse> formInputResponses = newFormInputResponse().build(3);
        when(restServiceMock.getResponsesByApplicationId(123L)).thenReturn(formInputResponses);

        List<FormInputResponse> response = service.getByApplication(123L);
        assertEquals(formInputResponses, response);
    }

    @Test
    public void test_mapResponsesToFormInputs() {

        List<FormInput> formInputs = reverse(newFormInput().build(3));

        List<FormInputResponse> formInputResponses = newFormInputResponse().
                withFormInputs(formInputs).
                build(3);

        Map<Long, FormInputResponse> response = service.mapFormInputResponsesToFormInput(formInputResponses);
        assertEquals(formInputResponses.get(0), response.get(3L));
        assertEquals(formInputResponses.get(1), response.get(2L));
        assertEquals(formInputResponses.get(2), response.get(1L));
    }

    @Test
    public void test_save() {

        when(restServiceMock.saveQuestionResponse(123L, 456L, 789L, "A new value")).
                thenReturn(asList("an error", "another error"));

        List<String> responses = service.save(123L, 456L, 789L, "A new value");
        assertEquals(asList("an error", "another error"), responses);
    }
}
