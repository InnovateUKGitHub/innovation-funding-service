package com.worth.ifs.project.util;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.project.PartnerOrganisationService;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * unit test for {@link ControllersUtil}
 */
public class ControllersUtilTest extends BaseUnitTestMocksTest{

    @Mock
    private PartnerOrganisationService partnerOrganisationService;

    @Test
    public void testIsLeadPartner() {
        Long projectId = 1L;
        Long organisationId = 2L;
        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(true);
        when(partnerOrganisationService.getPartnerOrganisations(projectId)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResource)));

        assertTrue(ControllersUtil.isLeadPartner(partnerOrganisationService, projectId, organisationId));

    }

    @Test
    public void testNotLeadPartner() {
        Long projectId = 1L;
        Long organisationId = 2L;
        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);
        when(partnerOrganisationService.getPartnerOrganisations(projectId)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResource)));

        assertFalse(ControllersUtil.isLeadPartner(partnerOrganisationService, projectId, organisationId));

        Long mismatchOrganisationId = 3L;
        partnerOrganisationResource.setOrganisation(mismatchOrganisationId);
        partnerOrganisationResource.setLeadOrganisation(true);

        assertFalse(ControllersUtil.isLeadPartner(partnerOrganisationService, projectId, organisationId));
    }
}
