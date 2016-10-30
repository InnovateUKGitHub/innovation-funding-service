package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.organisationResourceListType;

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

        String expectedUrl = BaseRestServiceUnitTest.dataServicesUrl + organisationsUrl + "/findByApplicationId/123";
        List<OrganisationResource> returnedResponse = Arrays.asList(1,2,3).stream().map(i -> new OrganisationResource()).collect(Collectors.toList());// newOrganisationResource().build(3);
        ResponseEntity<List<OrganisationResource>> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), organisationResourceListType())).thenReturn(responseEntity);

        List<OrganisationResource> response = service.getOrganisationsByApplicationId(123L).getSuccessObject();
        assertEquals(3, response.size());
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getOrganisationById() {

        String expectedUrl = BaseRestServiceUnitTest.dataServicesUrl + organisationsUrl + "/findById/123";
        OrganisationResource returnedResponse =  new OrganisationResource();
        ResponseEntity<OrganisationResource> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), OrganisationResource.class)).thenReturn(responseEntity);

        OrganisationResource response = service.getOrganisationById(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void updateOrganisationNameAndRegistation() throws UnsupportedEncodingException {
        Long organisationId = 1L;

        OrganisationResource organisation = new OrganisationResource();
        organisation.setId(organisationId);
        organisation.setName("Vitruvius Stonework Limited");
        organisation.setCompanyHouseNumber("60674010");


        String organisationNameEncoded = UriUtils.encode(organisation.getName(), "UTF-8");

        setupPostWithRestResultExpectations(organisationsUrl + "/updateNameAndRegistration/" + organisationId + "?name=" + organisationNameEncoded + "&registration=" + organisation.getCompanyHouseNumber(), OrganisationResource.class, null, organisation, OK);

        OrganisationResource receivedResource = service.updateNameAndRegistration(organisation).getSuccessObject();

        Assert.assertEquals(organisation, receivedResource);
    }
}
