package com.worth.ifs.finance.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.user.builder.RoleResourceBuilder;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.*;
import static java.util.Arrays.asList;
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
    public void setup() throws Exception {;

        // Set up roles
        final Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        final Role collaboratorRole = newRole().withType(COLLABORATOR).build();
        final Role assessorRole = newRole().withType(ASSESSOR).build();

        // Create a compAdmin
        final RoleResource compAdminRole = RoleResourceBuilder.newRoleResource().withType(COMP_ADMIN).build();
        compAdmin = newUserResource().withRolesGlobal(asList(compAdminRole)).build();

        // Set up users on an organisation and application
        final long applicationId = 1l;
        organisation = newOrganisationResource().build();
        applicationFinance = newApplicationFinanceResource().withOrganisation(organisation.getId()).withApplication(applicationId).build();
        leadApplicant = newUserResource().build();
        assessor = newUserResource().build();
        collaborator = newUserResource().build();

        setupUserOnApplication(leadApplicantRole, leadApplicant, applicationId, organisation.getId());
        setupUserOnApplication(collaboratorRole, collaborator, applicationId, organisation.getId());
        setupUserOnApplication(assessorRole, assessor, applicationId, organisation.getId());


        // set up different users on an organisation and application to check that there is no bleed through of permissions
        final long otherApplicationId = 2l;
        otherOrganisation = newOrganisationResource().build();
        otherApplicationFinance = newApplicationFinanceResource().withOrganisation(otherOrganisation.getId()).withApplication(otherApplicationId).build();
        otherLeadApplicant = newUserResource().build();
        setupUserOnApplication(leadApplicantRole, otherLeadApplicant, otherApplicationId, otherOrganisation.getId());
    }

    private void setupUserOnApplication(final Role role, final UserResource userResource, final long applicationId, final long organisationId){
        when(roleRepositoryMock.findByName(role.getName())).thenReturn(asList(role));
        when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(userResource.getId(), role.getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
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
        assertTrue(rules.compAdminCanSeeApplicationFinancesForOrganisations(applicationFinance, compAdmin));
        assertTrue(rules.compAdminCanSeeApplicationFinancesForOrganisations(otherApplicationFinance, compAdmin));
    }
}