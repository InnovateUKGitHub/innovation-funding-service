package com.worth.ifs.finance.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
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
    public void compAdminCanSeeApplicationFinancesForOrganisations() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (user.equals(compAdminUser())) {
                    assertTrue(rules.compAdminCanSeeApplicationFinancesForOrganisations(applicationFinance, user));
                } else {
                    assertFalse(rules.compAdminCanSeeApplicationFinancesForOrganisations(applicationFinance, user));
                }
            });
        });
    }

    @Test
    public void projectFinanceUserCanGetFileEntryResourceForFinanceIdOfACollaborator() {
        allGlobalRoleUsers.forEach(user -> {
                if (user.equals(projectFinanceUser())) {
                    assertTrue(rules.projectFinanceUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, user));
                } else {
                    assertFalse(rules.projectFinanceUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, user));
                }
        });
    }

    @Test
    public void testUpdateCosts() {
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisation(applicationFinance, compAdmin));
    }

    @Test
    public void testAddCosts() {
        assertTrue(rules.consortiumCanAddACostToApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanAddACostToApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));

        assertFalse(rules.consortiumCanAddACostToApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanAddACostToApplicationFinanceForTheirOrganisation(applicationFinance, compAdmin));
    }

    @Test
    public void testLeadCanGetFileResourceForPartner() {
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, compAdmin));
    }

    @Test
    public void testCompAdminCanGetFileResourceForPartner(){
        assertTrue(rules.compAdminCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, compAdmin));
        assertFalse(rules.compAdminCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, collaborator));
        assertFalse(rules.compAdminCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, leadApplicant));
    }

    @Test
    public void testProjectFinanceUserCanGetFileResourceForPartner(){
        assertTrue(rules.projectFinanceUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, projectFinanceUser()));
        assertFalse(rules.projectFinanceUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, collaborator));
        assertFalse(rules.projectFinanceUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, leadApplicant));
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
    public void testCompAdminCanGetApplicationFinance(){
        assertTrue(rules.compAdminCanSeeApplicationFinancesForOrganisations(applicationFinance, compAdmin));
        assertFalse(rules.compAdminCanSeeApplicationFinancesForOrganisations(applicationFinance, collaborator));
        assertFalse(rules.compAdminCanSeeApplicationFinancesForOrganisations(applicationFinance, leadApplicant));
    }

    @Test
    public void testProjectFinanceCanGetApplicationFinance(){
        assertTrue(rules.projectFinanceCanSeeApplicationFinancesForOrganisations(applicationFinance, projectFinanceUser()));
        assertFalse(rules.projectFinanceCanSeeApplicationFinancesForOrganisations(applicationFinance, collaborator));
        assertFalse(rules.projectFinanceCanSeeApplicationFinancesForOrganisations(applicationFinance, leadApplicant));
    }

}
