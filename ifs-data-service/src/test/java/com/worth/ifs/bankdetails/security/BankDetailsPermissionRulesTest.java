package com.worth.ifs.bankdetails.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class BankDetailsPermissionRulesTest extends BasePermissionRulesTest<BankDetailsPermissionRules> {

    @Override
    protected BankDetailsPermissionRules supplyPermissionRulesUnderTest() {
        return new BankDetailsPermissionRules();
    }

    UserResource user;
    ProjectResource project;
    Role partnerRole;
    List<ProjectUser> partnerProjectUser;
    OrganisationResource organisationResource;
    BankDetailsResource bankDetailsResource;

    @Before
    public void setUp(){
        user = newUserResource().build();
        project = newProjectResource().build();
        partnerRole = newRole().build();
        partnerProjectUser = newProjectUser().build(1);
        organisationResource = newOrganisationResource().build();
        bankDetailsResource = newBankDetailsResource().withOrganisation(organisationResource.getId()).withProject(project.getId()).build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
    }

    @Test
    public void testPartnersCanSeeBankDetailsOfTheirOwnOrg() {
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(partnerProjectUser);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(project.getId(), user.getId(), organisationResource.getId(), partnerRole.getId())).thenReturn(partnerProjectUser.get(0));
        assertTrue(rules.partnersCanSeeTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void testNonPartnersCannotSeeBankDetails() {
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(Collections.emptyList());
        assertFalse(rules.partnersCanSeeTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void testPartnersCannotSeeBankDetailsOfAnotherOrganisation() {
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(partnerProjectUser);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(project.getId(), user.getId(), organisationResource.getId(), partnerRole.getId())).thenReturn(null);
        assertFalse(rules.partnersCanSeeTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationBankDetails(){
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(partnerProjectUser);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(project.getId(), user.getId(), organisationResource.getId(), partnerRole.getId())).thenReturn(partnerProjectUser.get(0));
        assertTrue(rules.partnersCanUpdateTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void testNonPartnersCannotUpdateBankDetails(){
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(Collections.emptyList());
        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void testPartnersCannotUpdateBankDetailsOfAnotherOrg(){
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(partnerProjectUser);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(project.getId(), user.getId(), organisationResource.getId(), partnerRole.getId())).thenReturn(null);
        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }
}
