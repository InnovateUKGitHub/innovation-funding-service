package org.innovateuk.ifs.project.util;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * unit test for {@link ControllersUtil}
 */
public class ControllersUtilTest extends BaseUnitTestMocksTest{

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Test
    public void testIsLeadPartner() {
        Long projectId = 1L;
        Long organisationId = 2L;
        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(true);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(singletonList(partnerOrganisationResource)));

        assertTrue(ControllersUtil.isLeadPartner(partnerOrganisationRestService, projectId, organisationId));

    }

    @Test
    public void testNotLeadPartner() {
        Long projectId = 1L;
        Long organisationId = 2L;
        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(singletonList(partnerOrganisationResource)));

        assertFalse(ControllersUtil.isLeadPartner(partnerOrganisationRestService, projectId, organisationId));

        Long mismatchOrganisationId = 3L;
        partnerOrganisationResource.setOrganisation(mismatchOrganisationId);
        partnerOrganisationResource.setLeadOrganisation(true);

        assertFalse(ControllersUtil.isLeadPartner(partnerOrganisationRestService, projectId, organisationId));
    }
}
