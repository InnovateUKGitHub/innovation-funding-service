package org.innovateuk.ifs.application.service;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompanyHouseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class OrganisationServiceImplTest extends BaseServiceUnitTest<OrganisationService> {

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private UserRestService userRestService;

    @Override
    protected OrganisationService supplyServiceUnderTest() {
        return new OrganisationServiceImpl(organisationRestService, userRestService);
    }

    @Test
    public void testGetOrganisationById() throws Exception {
        Long organisationId = 3L;
        OrganisationResource organisation = new OrganisationResource();
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

        OrganisationResource returnedOrganisation = service.getOrganisationById(organisationId);

        assertEquals(organisation, returnedOrganisation);
    }

    @Test
    public void testCreateOrMatch() throws Exception {
        OrganisationResource resourceToSave = new OrganisationResource();
        OrganisationResource organisation = new OrganisationResource();
        when(organisationRestService.createOrMatch(resourceToSave)).thenReturn(restSuccess(organisation));

        OrganisationResource returnedOrganisation = service.createOrMatch(resourceToSave);

        assertEquals(organisation, returnedOrganisation);
    }

    @Test
    public void testCreateAndLinkByInvite() throws Exception {
        String inviteHash = "123abc";

        OrganisationResource resourceToSave = new OrganisationResource();
        OrganisationResource organisation = new OrganisationResource();
        when(organisationRestService.createAndLinkByInvite(resourceToSave, inviteHash)).thenReturn(restSuccess(organisation));

        OrganisationResource returnedOrganisation = service.createAndLinkByInvite(resourceToSave, inviteHash);

        assertEquals(organisation, returnedOrganisation);
    }

    @Test
    public void testGetOrganisationType() throws Exception {
        Long userId = 2L;
        Long applicationId = 3L;
        Long organisationId = 4L;
        Long organisationType = 2L;
        ProcessRoleResource processRole = new ProcessRoleResource();
        processRole.setOrganisationId(organisationId);
        OrganisationResource organisation = new OrganisationResource();
        organisation.setOrganisationType(organisationType);
        when(userRestService.findProcessRole(userId, applicationId)).thenReturn(restSuccess(processRole));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

        Long returnedOrganisationType = service.getOrganisationType(userId, applicationId);

        assertEquals(organisationType, returnedOrganisationType);
    }

    @Test
    public void testGetOrganisationForUser() throws Exception {
        Long userId = 2L;
        Long organisationId = 4L;
        ProcessRoleResource roleWithUser = new ProcessRoleResource();
        roleWithUser.setUser(userId);
        roleWithUser.setOrganisationId(organisationId);
        ProcessRoleResource roleWithoutUser = new ProcessRoleResource();
        roleWithoutUser.setUser(3L);
        OrganisationResource organisation = new OrganisationResource();
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

        Optional<OrganisationResource> result = service.getOrganisationForUser(userId, Lists.newArrayList(roleWithUser, roleWithoutUser));

        assertEquals(organisation, result.get());
    }
}
