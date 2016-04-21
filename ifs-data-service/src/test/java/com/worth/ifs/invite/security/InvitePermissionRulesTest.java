package com.worth.ifs.invite.security;


import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.user.builder.OrganisationBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.invite.builder.InviteBuilder.newInvite;
import static com.worth.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class InvitePermissionRulesTest extends BasePermissionRulesTest<InvitePermissionRules> {

    private UserResource leadApplicant;
    private Invite invite;


    @Override
    protected InvitePermissionRules supplyPermissionRulesUnderTest() {
        return new InvitePermissionRules();
    }

    @Before
    public void setup() throws Exception {
        leadApplicant = newUserResource().build();
        final Application application = newApplication().build();
        final Organisation organisation = OrganisationBuilder.newOrganisation().build();
        final InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisation(organisation).build();
        invite = newInvite().withApplication(application).withInviteOrganisation(inviteOrganisation).build();

        when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicant.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
    }

    @Test
    public void testLeadApplicantCanInviteToTheApplication() {
        assertTrue(rules.leadApplicantCanInviteToTheApplication(invite, leadApplicant));
    }

    @Test
    public void testCollaboratorCanInviteToApplicantForTheirOrganisation() {

    }

    @Test
    public void testLeadApplicantCanSaveInviteToTheApplication() {

    }

    @Test
    public void testCollaboratorCanSaveInviteToApplicantForTheirOrganisation() {

    }

    @Test
    public void testCollaboratorCanReadInviteForTheirApplicationForTheirOrganisation() {

    }

    @Test
    public void testLeadApplicantReadInviteToTheApplication() {

    }


}
