package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

import org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PartnerOrganisationRestServiceImplTest extends BaseRestServiceUnitTest<PartnerOrganisationRestServiceImpl> {

    private static final String projectRestURL = "/project";

    @Test
    public void testGetProjectPartnerOrganisations(){
        Long projectId = 123L;
        List<PartnerOrganisationResource> partnerOrganisations = Arrays.asList(1,2,3).stream().map(i -> {
            PartnerOrganisationResource x = new PartnerOrganisationResource();
            x.setProject(projectId);
            return x;
        }).collect(Collectors.toList());

        setupGetWithRestResultExpectations(projectRestURL + "/123/partner-organisation", partnerOrganisationResourceList(), partnerOrganisations);
        RestResult result = service.getProjectPartnerOrganisations(projectId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetPartnerOrganisation() {

        long projectId = 1L;
        long organisationId = 2L;

        PartnerOrganisationResource partnerOrganisationResource = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource().build();

        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/partner/" + organisationId, PartnerOrganisationResource.class, partnerOrganisationResource);

        RestResult<PartnerOrganisationResource> result = service.getPartnerOrganisation(projectId, organisationId);
        assertTrue(result.isSuccess());
        assertEquals(partnerOrganisationResource, result.getSuccess());

        setupGetWithRestResultVerifications(projectRestURL + "/" + projectId + "/partner/" + organisationId, null, PartnerOrganisationResource.class);
    }

    @Override
    protected PartnerOrganisationRestServiceImpl registerRestServiceUnderTest() {
        PartnerOrganisationRestServiceImpl partnerOrganisationRestService = new PartnerOrganisationRestServiceImpl();
        ReflectionTestUtils.setField(partnerOrganisationRestService, "projectRestURL", projectRestURL);
        return partnerOrganisationRestService;
    }
}
