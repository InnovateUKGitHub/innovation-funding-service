package org.innovateuk.ifs.user.service;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationResourceListType;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationExecutiveOfficerResourceBuilder.newOrganisationExecutiveOfficerResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationSicCodeResourceBuilder.newOrganisationSicCodeResource;
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
    public void getOrganisations() {
        long userId = 1L;
        OrganisationResource organisationResource = newOrganisationResource().build();

        List<OrganisationResource> organisationResources = ImmutableList.of(organisationResource);

        setupGetWithRestResultExpectations(format("%s?userId=%s&international=%s", ORGANISATION_BASE_URL, userId, "false"), organisationResourceListType(), organisationResources);

        List<OrganisationResource> results = service.getOrganisations(userId, false).getSuccess();

        assertEquals(1, results.size());
        assertEquals(results, organisationResources);
    }

    @Test
    public void syncCompaniesHouseDetails() {
        OrganisationResource organisation = newOrganisationResource().build();
        OrganisationResource organisationDataResource = newOrganisationResource()
                .withDateOfIncorporation(LocalDate.now())
                .build();
        List<OrganisationSicCodeResource> sicCodeResources = newOrganisationSicCodeResource()
                .withSicCode("12345", "67890")
                .withOrganisation(organisationDataResource.getId())
                .build(2);
        organisationDataResource.setSicCodes(sicCodeResources);
        List<OrganisationExecutiveOfficerResource> executiveOfficerResources = newOrganisationExecutiveOfficerResource()
                .withName("Name-1", "Name-2")
                .withOrganisation(organisationDataResource.getId())
                .build(2);
        organisationDataResource.setExecutiveOfficers(executiveOfficerResources);
        AddressResource addressResource = newAddressResource()
                .withAddressLine1("address line 1")
                .withAddressLine2("address line 2")
                .withAddressLine3("address line 3")
                .withTown("town")
                .withCounty("county")
                .withCountry("country")
                .build();
        AddressTypeResource addressTypeResource = newAddressTypeResource()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource()
                .withAddress(addressResource)
                .withAddressType(addressTypeResource)
                .withOrganisation(organisationDataResource.getId())
                .build();
        organisationDataResource.setAddresses(Collections.singletonList(organisationAddressResource));

        setupPutWithRestResultExpectations(ORGANISATION_BASE_URL + "/sync-companies-house-details", OrganisationResource.class, organisation, organisationDataResource, OK);
        OrganisationResource receivedResource = service.syncCompaniesHouseDetails(organisation).getSuccess();

        Assert.assertEquals(organisationDataResource, receivedResource);
    }
}
