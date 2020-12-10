package org.innovateuk.ifs.heukar.service;

import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.heukarPartnerOrganisationResourceListType;
import static org.innovateuk.ifs.organisation.builder.HeukarPartnerOrganisationResourceBuilder.newHeukarPartnerOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.junit.Assert.assertTrue;

public class HeukarPartnerOrganisationRestServiceImplTest extends BaseRestServiceUnitTest<HeukarPartnerOrganisationRestServiceImpl> {

    private static final String heukarUrl = "heukar-partner-organisation";

    @Test
    public void getHeukarPartnerOrganisationsForApplicationWithId() {
        long applicationId = 1;

        List<HeukarPartnerOrganisationResource> resourceList = newHeukarPartnerOrganisationResource()
                .withId(1L)
                .withApplicationId(1L)
                .withOrganisationTypeResource(newOrganisationTypeResource().withName("test").withId(1L).build())
                .build(2);

        setupGetWithRestResultExpectations(heukarUrl + "/find-by-application-id/" + applicationId, heukarPartnerOrganisationResourceListType(), resourceList);
        RestResult<List<HeukarPartnerOrganisationResource>> result = service.getHeukarPartnerOrganisationsForApplicationWithId(applicationId);
        assertThat(result.getSuccess(), Matchers.equalTo(resourceList));
    }

    @Test
    public void addNewHeukarOrgType() {
        long applicationId = 1;
        long organisationTypeId = 1;
        setupPostWithRestResultExpectations(heukarUrl + "/add-new-org-type/" + applicationId + "/" + organisationTypeId, Void.class, null, null, HttpStatus.CREATED);
        RestResult<Void> restResult = service.addNewHeukarOrgType(applicationId, organisationTypeId);
        assertTrue(restResult.isSuccess());
    }


    @Test
    public void removeHeukarPartnerOrganisation() {
        long existingId = 1;
        setupDeleteWithRestResultExpectations(heukarUrl + "/" + existingId, HttpStatus.OK);
        RestResult<Void> restResult = service.removeHeukarPartnerOrganisation(existingId);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void getExistingPartnerById() {
        HeukarPartnerOrganisationResource resource = newHeukarPartnerOrganisationResource()
                .withId(1L)
                .withApplicationId(1L)
                .withOrganisationTypeResource(newOrganisationTypeResource().withName("test").withId(1L).build())
                .build();

        setupGetWithRestResultExpectations(heukarUrl + "/" + resource.getId(), HeukarPartnerOrganisationResource.class, resource);
        RestResult<HeukarPartnerOrganisationResource> restResult = service.getExistingPartnerById(resource.getId());
        assertTrue(restResult.isSuccess());
    }

    @Override
    protected HeukarPartnerOrganisationRestServiceImpl registerRestServiceUnderTest() {
        return new HeukarPartnerOrganisationRestServiceImpl();
    }
}