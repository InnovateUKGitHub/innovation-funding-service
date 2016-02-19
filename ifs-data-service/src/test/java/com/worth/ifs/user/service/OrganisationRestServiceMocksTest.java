package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
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
        List<Organisation> returnedResponse = newOrganisation().build(3);
        ResponseEntity<List<Organisation>> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), new ParameterizedTypeReference<List<Organisation>>() {})).thenReturn(responseEntity);

        List<Organisation> response = service.getOrganisationsByApplicationId(123L).getSuccessObject();
        assertEquals(3, response.size());
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getOrganisationById() {

        String expectedUrl = dataServicesUrl + organisationsUrl + "/findById/123";
        Organisation returnedResponse = newOrganisation().build();
        ResponseEntity<Organisation> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Organisation.class)).thenReturn(responseEntity);

        Organisation response = service.getOrganisationById(123L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }
}
