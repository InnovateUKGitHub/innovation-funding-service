package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;


public class OrganisationRestServiceMocksTest extends BaseRestServiceUnitTest<OrganisationRestServiceImpl> {

    private static final String organisationsUrl = "/organisation";

    @Override
    protected OrganisationRestServiceImpl registerRestServiceUnderTest() {
        OrganisationRestServiceImpl userRestService = new OrganisationRestServiceImpl();
        userRestService.organisationRestURL = organisationsUrl;
        return userRestService;
    }

    @Test
    public void test_getOrganisationsByApplicationId() {

        String expectedUrl = dataServicesUrl + organisationsUrl + "/findByApplicationId/123";
        Organisation[] returnedResponse = newOrganisation().buildArray(3, Organisation.class);
        ResponseEntity<Organisation[]> responseEntity = new ResponseEntity<Organisation[]>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Organisation[].class)).thenReturn(responseEntity);

        List<Organisation> response = service.getOrganisationsByApplicationId(123L);
        assertEquals(3, response.size());
        assertEquals(returnedResponse[0], response.get(0));
        assertEquals(returnedResponse[1], response.get(1));
        assertEquals(returnedResponse[2], response.get(2));
    }
}
