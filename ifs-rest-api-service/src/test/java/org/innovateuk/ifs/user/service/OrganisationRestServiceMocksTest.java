package org.innovateuk.ifs.user.service;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationResourceListType;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;


public class OrganisationRestServiceMocksTest extends BaseRestServiceUnitTest<OrganisationRestServiceImpl> {

    private static final String ORGANISATION_BASE_URL = "/organisation";

    @Override
    protected OrganisationRestServiceImpl registerRestServiceUnderTest() {
        return new OrganisationRestServiceImpl();
    }

    @Test
     public void test_getOrganisationsByApplicationId() {

        String expectedUrl = BaseRestServiceUnitTest.dataServicesUrl + ORGANISATION_BASE_URL + "/find-by-application-id/123";
        List<OrganisationResource> returnedResponse = Arrays.asList(1,2,3).stream().map(i -> new OrganisationResource()).collect(Collectors.toList());// newOrganisationResource().build(3);
        ResponseEntity<List<OrganisationResource>> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), organisationResourceListType())).thenReturn(responseEntity);

        List<OrganisationResource> response = service.getOrganisationsByApplicationId(123L).getSuccess();
        assertEquals(3, response.size());
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getOrganisationById() {

        String expectedUrl = BaseRestServiceUnitTest.dataServicesUrl + ORGANISATION_BASE_URL + "/find-by-id/123";
        OrganisationResource returnedResponse =  new OrganisationResource();
        ResponseEntity<OrganisationResource> responseEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), OrganisationResource.class)).thenReturn(responseEntity);

        OrganisationResource response = service.getOrganisationById(123L).getSuccess();
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void updateOrganisationNameAndRegistation() throws UnsupportedEncodingException {
        Long organisationId = 1L;

        OrganisationResource organisation = new OrganisationResource();
        organisation.setId(organisationId);
        organisation.setName("Vitruvius Stonework Limited");
        organisation.setCompaniesHouseNumber("60674010");


        String organisationNameEncoded = UriUtils.encode(organisation.getName(), "UTF-8");

        setupPostWithRestResultExpectations(ORGANISATION_BASE_URL + "/update-name-and-registration/" + organisationId + "?name=" + organisationNameEncoded + "&registration=" + organisation.getCompaniesHouseNumber(), OrganisationResource.class, null, organisation, OK);
        OrganisationResource receivedResource = service.updateNameAndRegistration(organisation).getSuccess();

        Assert.assertEquals(organisation, receivedResource);
    }

    @Test
    public void createOrMatch() {
        OrganisationResource expected = newOrganisationResource().build();

        setupPostWithRestResultAnonymousExpectations(format("%s/create-or-match", ORGANISATION_BASE_URL), OrganisationResource.class, expected, expected, OK);

        OrganisationResource response = service.createOrMatch(expected).getSuccess();
        assertEquals(expected, response);
    }

    @Test
    public void getByUserAndApplicationId() {
        long userId = 1L;
        long applicationId = 2L;
        OrganisationResource organisationResource = newOrganisationResource().build();
        setupGetWithRestResultExpectations(format("%s/by-user-and-application-id/%s/%s", ORGANISATION_BASE_URL, userId, applicationId), OrganisationResource.class, organisationResource);

        OrganisationResource result = service.getByUserAndApplicationId(userId, applicationId).getSuccess();

        assertEquals(result, organisationResource);
    }

    @Test
    public void getByUserAndProjectId() {
        long userId = 1L;
        long projectId = 2L;
        OrganisationResource organisationResource = newOrganisationResource().build();
        setupGetWithRestResultExpectations(format("%s/by-user-and-project-id/%s/%s", ORGANISATION_BASE_URL, userId, projectId), OrganisationResource.class, organisationResource);

        OrganisationResource result = service.getByUserAndProjectId(userId, projectId).getSuccess();

        assertEquals(result, organisationResource);
    }

    @Test
    public void getUkBasedOrganisations() {
        long userId = 1L;
        OrganisationResource ukBasedOrganisation = newOrganisationResource().withIsInternational(true).build();
        List<OrganisationResource> organisationResource = ImmutableList.of(ukBasedOrganisation);

        setupGetWithRestResultExpectations(format("%s?userId=%s&international=%s", ORGANISATION_BASE_URL, userId, "false"), organisationResourceListType(), organisationResource);

        List<OrganisationResource> results = service.getOrganisations(userId, false).getSuccess();

        assertEquals(1, results.size());
        assertEquals(results, organisationResource);
    }

    @Test
    public void getInternationalOrganisations() {
        long userId = 1L;
        OrganisationResource internationalOrganisation = newOrganisationResource().withIsInternational(true).build();
        List<OrganisationResource> organisationResource = ImmutableList.of(internationalOrganisation);

        setupGetWithRestResultExpectations(format("%s?userId=%s&international=%s", ORGANISATION_BASE_URL, userId, "true"), organisationResourceListType(), organisationResource);

        List<OrganisationResource> results = service.getOrganisations(userId, true).getSuccess();

        assertEquals(1, results.size());
        assertEquals(results, organisationResource);
    }
}
