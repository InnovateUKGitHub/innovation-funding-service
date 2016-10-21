package com.worth.ifs.project.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import com.worth.ifs.project.builder.PartnerOrganisationResourceBuilder;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.worth.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.junit.Assert.assertTrue;

public class PartnerOrganisationRestServiceImplTest extends BaseRestServiceUnitTest<PartnerOrganisationRestServiceImpl> {

    private static final String projectRestURL = "/project";

    @Test
    public void testGetProjectPartnerOrganisations(){
        Long projectId = 123L;
        List<PartnerOrganisationResource> partnerOrganisations = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource().withProject(projectId).build(3);
        setupGetWithRestResultExpectations(projectRestURL + "/123/partner-organisation", ParameterizedTypeReferences.partnerOrganisationResourceList(), partnerOrganisations);
        RestResult result = service.getProjectPartnerOrganisations(projectId);
        assertTrue(result.isSuccess());
    }

    @Override
    protected PartnerOrganisationRestServiceImpl registerRestServiceUnderTest() {
        PartnerOrganisationRestServiceImpl partnerOrganisationRestService = new PartnerOrganisationRestServiceImpl();
        ReflectionTestUtils.setField(partnerOrganisationRestService, "projectRestURL", projectRestURL);
        return partnerOrganisationRestService;
    }
}
