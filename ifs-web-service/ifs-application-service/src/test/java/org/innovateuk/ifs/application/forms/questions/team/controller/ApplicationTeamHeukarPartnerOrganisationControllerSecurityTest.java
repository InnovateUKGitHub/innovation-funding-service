package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationTeamHeukarPartnerOrganisationControllerSecurityTest extends BaseControllerSecurityTest<ApplicationTeamHeukarPartnerOrganisationController> {

    @Override
    protected Class<? extends ApplicationTeamHeukarPartnerOrganisationController> getClassUnderTest() {
        return ApplicationTeamHeukarPartnerOrganisationController.class;
    }

    @Test
    public void showAddNewPartnerOrganisationForm() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.LEADAPPLICANT);

        assertRolesCanPerform(() -> classUnderTest.showAddNewPartnerOrganisationForm(
                null,
                null,
                null,
                1L,
                1L), roles);
    }
}