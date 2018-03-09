package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationFinancePermissionRulesTest extends BasePermissionRulesTest<ApplicationFinancePermissionRules> {

    private ApplicationFinanceResource applicationFinance;
    private OrganisationResource organisation;
    private UserResource assessor;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;

    private ApplicationFinanceResource otherApplicationFinance;
    private UserResource otherLeadApplicant;
    private OrganisationResource otherOrganisation;

    @Override
    protected ApplicationFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationFinancePermissionRules();
    }

    @Before
    public void setup() throws Exception {

        // Create a compAdmin
        compAdmin = this.compAdminUser();

        {
            // Set up users on an organisation and application
            final long applicationId = 1L;
            final long organisationId = 2L;
            organisation = newOrganisationResource().with(id(organisationId)).build();
            applicationFinance = newApplicationFinanceResource().withOrganisation(organisation.getId()).withApplication(applicationId).build();
            leadApplicant = newUserResource().build();
            assessor = newUserResource().build();
            collaborator = newUserResource().build();

            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(leadApplicant.getId(), getRole(LEADAPPLICANT).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(leadApplicant.getId(), getRole(LEADAPPLICANT).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaborator.getId(), getRole(COLLABORATOR).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(assessor.getId(), getRole(ASSESSOR).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());

            Role compAdminRole = newRole().withType(UserRoleType.COMP_ADMIN).build();

            ProcessRole compAdminProcessRole = newProcessRole().withRole(compAdminRole).build();

            when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRoleName(leadApplicant.getId(), applicationId, LEADAPPLICANT.getName())).thenReturn(true);
            when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRoleName(collaborator.getId(), applicationId, COLLABORATOR.getName())).thenReturn(true);
            when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRoleName(assessor.getId(), applicationId, ASSESSOR.getName())).thenReturn(true);
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(compAdmin.getId(), applicationId)).thenReturn(compAdminProcessRole);
        }
        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3L;
            final long otherOrganisationId = 4L;
            otherOrganisation = newOrganisationResource().with(id(otherOrganisationId)).build();
            otherApplicationFinance = newApplicationFinanceResource().withOrganisation(otherOrganisation.getId()).withApplication(otherApplicationId).build();
            otherLeadApplicant = newUserResource().build();
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(), getRole(LEADAPPLICANT).getId(), otherApplicationId, otherOrganisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRoleName(otherLeadApplicant.getId(), otherApplicationId, LEADAPPLICANT.getName())).thenReturn(true);
        }
    }

    @Test
    public void consortiumCanSeeTheApplicationFinancesForTheirOrganisationTest() {
        assertTrue(rules.consortiumCanSeeTheApplicationFinancesForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanSeeTheApplicationFinancesForTheirOrganisation(applicationFinance, collaborator));

        assertTrue(rules.consortiumCanSeeTheApplicationFinancesForTheirOrganisation(otherApplicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanSeeTheApplicationFinancesForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void assessorCanSeeTheApplicationFinanceForOrganisationsInApplicationsTheyAssess() {
        assertTrue(rules.assessorCanSeeTheApplicationFinanceForOrganisationsInApplicationsTheyAssess(applicationFinance, assessor));
        assertFalse(rules.assessorCanSeeTheApplicationFinanceForOrganisationsInApplicationsTheyAssess(otherApplicationFinance, assessor));
    }

    @Test
    public void internalUserCanSeeApplicationFinancesForOrganisations() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (allInternalUsers.contains(user)) {
                    assertTrue(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, user));
                } else {
                    assertFalse(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, user));
                }
            });
        });
    }

    @Test
    public void testUpdateCosts() {
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, collaborator));

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, compAdmin));
    }

    @Test
    public void testAddCosts() {
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, collaborator));
        assertTrue(rules.supportCanAddACostToApplicationFinance(applicationFinance, supportUser()));
        assertTrue(rules.innovationLeadCanAddACostToApplicationFinance(applicationFinance, innovationLeadUser()));
        assertTrue(rules.assessorCanAddACostToApplicationFinance(applicationFinance, assessor));

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, compAdmin));
        assertFalse(rules.supportCanAddACostToApplicationFinance(applicationFinance, compAdmin));
        assertFalse(rules.innovationLeadCanAddACostToApplicationFinance(applicationFinance, compAdmin));
    }

    @Test
    public void testLeadCanGetFileResourceForPartner() {
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, collaborator));
        assertTrue(rules.assessorUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, assessor));
        assertFalse(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, compAdmin));
    }

    @Test
    public void testInternalUserCanGetFileResourceForPartner(){
        assertTrue(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, compAdmin));
        assertTrue(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, projectFinanceUser()));
        assertFalse(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, collaborator));
        assertFalse(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, leadApplicant));
    }

    @Test
    public void testConsortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(){
        assertTrue(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void testConsortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(){
        assertTrue(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void testConsortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(){
        assertTrue(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void testInternalUserCanGetApplicationFinance(){
        assertTrue(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, compAdmin));
        assertTrue(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, projectFinanceUser()));
        assertFalse(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, collaborator));
        assertFalse(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, leadApplicant));
    }
}
