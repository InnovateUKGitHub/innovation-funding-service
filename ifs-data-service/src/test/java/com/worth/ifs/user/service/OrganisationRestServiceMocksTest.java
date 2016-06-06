package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.organisationResourceListType;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;


public class OrganisationRestServiceMocksTest extends BaseRestServiceUnitTest<OrganisationRestServiceImpl> {

    private static final String organisationsUrl = "/organisation";

    @Override
    protected OrganisationRestServiceImpl registerRestServiceUnderTest() {
        OrganisationRestServiceImpl userRestService = new OrganisationRestServiceImpl();
        return userRestService;
    }

    @Test
     public void test_getOrganisationsByApplicationId() {

        String expectedUrl = dataServicesUrl + organisationsUrl + "/findByApplicationId/123";
        List<OrganisationResource> returnedResponse = newOrganisationResource().build(3);
        ResponseEntity<List<OrganisationResource>> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), organisationResourceListType())).thenReturn(responseEntity);

        List<OrganisationResource> response = service.getOrganisationsByApplicationId(123L).getSuccessObject();
        assertEquals(3, response.size());
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getOrganisationById() {

        String expectedUrl = dataServicesUrl + organisationsUrl + "/findById/123";
        OrganisationResource returnedResponse = newOrganisationResource().build();
        ResponseEntity<OrganisationResource> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), OrganisationResource.class)).thenReturn(responseEntity);

        OrganisationResource response = service.getOrganisationById(123L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }
}
