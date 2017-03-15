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
        ;

        // Create a compAdmin
        compAdmin = this.compAdminUser();

        {
            // Set up users on an organisation and application
            final long applicationId = 1l;
            final long organisationId = 2l;
            organisation = newOrganisationResource().with(id(organisationId)).build();
            applicationFinance = newApplicationFinanceResource().withOrganisation(organisation.getId()).withApplication(applicationId).build();
            leadApplicant = newUserResource().build();
            assessor = newUserResource().build();
            collaborator = newUserResource().build();

            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(leadApplicant.getId(), getRole(LEADAPPLICANT).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaborator.getId(), getRole(COLLABORATOR).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(assessor.getId(), getRole(ASSESSOR).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());

            Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
            Role collaboratorRole = newRole().withType(UserRoleType.COLLABORATOR).build();
            Role compAdminRole = newRole().withType(UserRoleType.COMP_ADMIN).build();

            ProcessRole leadApplicantProcessRole = newProcessRole().withRole(leadApplicantRole).build();
            ProcessRole collaboratorProcessRole = newProcessRole().withRole(collaboratorRole).build();
            ProcessRole compAdminProcessRole = newProcessRole().withRole(compAdminRole).build();

            when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicant.getId(), applicationId)).thenReturn(leadApplicantProcessRole);
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaborator.getId(), applicationId)).thenReturn(collaboratorProcessRole);
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(compAdmin.getId(), applicationId)).thenReturn(compAdminProcessRole);

        }
        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3l;
            final long otherOrganisationId = 4l;
            otherOrganisation = newOrganisationResource().with(id(otherOrganisationId)).build();
            otherApplicationFinance = newApplicationFinanceResource().withOrganisation(otherOrganisation.getId()).withApplication(otherApplicationId).build();
            otherLeadApplicant = newUserResource().build();
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(), getRole(LEADAPPLICANT).getId(), otherApplicationId, otherOrganisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndApplicationId(otherLeadApplicant.getId(), otherApplicationId)).thenReturn(newProcessRole().withRole(LEADAPPLICANT).build());

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

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, compAdmin));
    }

    @Test
    public void testLeadCanGetFileResourceForPartner() {
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, collaborator));
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
