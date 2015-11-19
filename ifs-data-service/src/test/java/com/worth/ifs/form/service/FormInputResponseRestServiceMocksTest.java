package com.worth.ifs.form.service;

import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class FormInputResponseRestServiceMocksTest extends BaseRestServiceMocksTest<FormInputResponseRestServiceImpl> {

    private static final String formInputResponseRestURL = "/forminputresponses";

    @Override
    protected FormInputResponseRestServiceImpl registerRestServiceUnderTest() {
        FormInputResponseRestServiceImpl formInputResponseService = new FormInputResponseRestServiceImpl();
        formInputResponseService.formInputResponseRestURL = formInputResponseRestURL;
        return formInputResponseService;
    }

    @Test
    public void test_getResponsesByApplicationId() {

        String expectedUrl = dataServicesUrl + formInputResponseRestURL + "/findResponsesByApplication/123";
        FormInputResponse[] returnedResponses = newFormInputResponse().buildArray(3, FormInputResponse.class);
        ResponseEntity<FormInputResponse[]> returnedEntity = new ResponseEntity<>(returnedResponses, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), FormInputResponse[].class)).thenReturn(returnedEntity);

        List<FormInputResponse> responses = service.getResponsesByApplicationId(123L);
        assertNotNull(responses);
    }
}
