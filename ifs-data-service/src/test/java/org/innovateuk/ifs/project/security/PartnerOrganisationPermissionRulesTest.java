package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PartnerOrganisationPermissionRulesTest extends BasePermissionRulesTest<PartnerOrganisationPermissionRules> {

    @Override
    protected PartnerOrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new PartnerOrganisationPermissionRules();
    }

    @Test
    public void testInternalUsersCanView() {

        UserResource user = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();

        assertTrue(rules.internalUsersCanViewPartnerOrganisations(partnerOrg, user));
    }

    @Test
    public void testExternalUsersCannotView() {

        UserResource user = newUserResource().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();

        assertFalse(rules.internalUsersCanViewPartnerOrganisations(partnerOrg, user));
    }
}
