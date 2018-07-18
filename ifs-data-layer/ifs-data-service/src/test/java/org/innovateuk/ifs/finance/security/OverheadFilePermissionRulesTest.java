package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OverheadFilePermissionRulesTest extends BasePermissionRulesTest<OverheadFilePermissionRules> {

    private FinanceRow overheads;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;
    private UserResource otherLeadApplicant;
    private UserResource internalUser;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepositoryMock;

    @Override
    protected OverheadFilePermissionRules supplyPermissionRulesUnderTest() {
        return new OverheadFilePermissionRules();
    }

    @Before
    public void setup() throws Exception {

        // Create a compAdmin
        compAdmin = compAdminUser();
        {
            // Set up users on an organisation and application
            final Long applicationId = 1L;
            final Long organisationId = 2L;
            final Application application = newApplication().with(id(applicationId)).build();
            final Organisation organisation = newOrganisation().with(id(organisationId)).build();
            final ApplicationFinance applicationFinance = newApplicationFinance().withApplication(application).withOrganisation(organisation).build();
            overheads = newApplicationFinanceRow().withOwningFinance(applicationFinance).build();

            leadApplicant = newUserResource().build();
            collaborator = newUserResource().build();
            when(applicationFinanceRowRepositoryMock.findById(overheads.getId())).thenReturn(Optional.of((ApplicationFinanceRow) overheads));
            when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicant.getId(), Role.LEADAPPLICANT, applicationId, organisationId)).
                    thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaborator.getId(), Role.COLLABORATOR, applicationId, organisationId)).
                    thenReturn(newProcessRole().build());
        }

        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3l;
            final long otherOrganisationId = 4l;
            otherLeadApplicant = newUserResource().build();
            when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(),
                    Role.LEADAPPLICANT, otherApplicationId, otherOrganisationId)).thenReturn(newProcessRole().build());
        }

        // Create project with users for testing getting of partner funding status
        {
            final Long projectId = 1L;
            final Long userId = 1L;
            when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(projectId, userId, PROJECT_PARTNER)).
                    thenReturn(Collections.singletonList(newProjectUser().withId(userId).build()));
        }

        // Create differnet users with different project
        {
            final Long otherProjectProjectId = 2L;
            final Long otherProjectUserId = 2L;
            when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(otherProjectProjectId, otherProjectUserId, PROJECT_PARTNER)).
                    thenReturn(Collections.singletonList(newProjectUser().withId(otherProjectUserId).build()));
        }
        //Setting internal user attributes
        {
            internalUser = newUserResource().withRolesGlobal(asList(Role.COMP_ADMIN)).build();
        }
    }

    @Test
    public void testConsortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void testConsortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void testConsortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void testConsortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void testConsortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void testInternalUserCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.internalUserCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, internalUser));
        assertFalse(rules.internalUserCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
    }

    @Test
    public void testInternalUserCanReadDetailsOfAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.internalUserCanReadDetailsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, internalUser));
        assertFalse(rules.internalUserCanReadDetailsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
    }
}
