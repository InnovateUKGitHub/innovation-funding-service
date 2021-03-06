package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.Optional;

import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PartnerOrganisationPermissionRulesTest extends BasePermissionRulesTest<PartnerOrganisationPermissionRules> {

    @Override
    protected PartnerOrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new PartnerOrganisationPermissionRules();
    }

    @Test
    public void partnersCanView() {

        UserResource user = newUserResource().withRoleGlobal(COMP_ADMIN).build();
        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().withProject(project).build();

        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(singletonList(projectUser));

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertTrue(rules.partnersOnProjectCanView(partnerOrg, user));
    }

    @Test
    public void nonPartnersCannotView() {

        UserResource user = newUserResource().withRoleGlobal(COMP_ADMIN).build();
        Project project = newProject().build();

        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(emptyList());

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertFalse(rules.partnersOnProjectCanView(partnerOrg, user));
    }

    @Test
    public void internalUserCanView() {

        UserResource user = newUserResource().withRoleGlobal(COMP_ADMIN).build();
        Project project = newProject().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertTrue(rules.internalUsersCanView(partnerOrg, user));
    }

    @Test
    public void monitoringOfficerCanView() {

        UserResource user = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();
        Project project = newProject().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();
        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), user.getId())).thenReturn(true);

        assertTrue(rules.monitoringOfficersUsersCanView(partnerOrg, user));
    }

    @Test
    public void externalFinanceUserCanView() {

        long projectId = 1L;
        Competition competition = newCompetition().build();
        Organisation organisation = newOrganisation().build();
        UserResource user = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        Project project = newProject().withId(projectId).withApplication(newApplication()
                .withCompetition(competition).build()).build();
        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withOrganisation(organisation.getId()).withProject(projectId).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUsersCanViewProjects(partnerOrg, user));
    }


    @Test
    public void externalUsersCannotView() {

        UserResource user = newUserResource().withRoleGlobal(APPLICANT).build();
        Project project = newProject().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertFalse(rules.internalUsersCanView(partnerOrg, user));
    }

    @Test
    public void partnersCannotViewOtherPendingPartnerProgress() {

        long projectId = 1L;
        long organisationId = 2L;
        UserResource user = newUserResource().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource()
                .withProject(projectId)
                .withOrganisation(organisationId)
                .build();
        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(projectId, user.getId(), organisationId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(null);

        assertFalse(rules.partnersCanReadTheirOwnPendingPartnerProgress(partnerOrg, user));
    }

    @Test
    public void partnersCanViewTheirOwnPendingPartnerProgress() {

        long projectId = 1L;
        long organisationId = 2L;
        UserResource user = newUserResource().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource()
                .withProject(projectId)
                .withOrganisation(organisationId)
                .build();
        ProjectUser projectUser = newProjectUser()
                .build();
        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(projectId, user.getId(), organisationId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(projectUser);

        assertTrue(rules.partnersCanReadTheirOwnPendingPartnerProgress(partnerOrg, user));
    }

    @Test
    public void internalUsersCanViewPendingPartnerProgress() {

        long projectId = 1L;
        long organisationId = 2L;
        UserResource user = newUserResource().build();
        user.isInternalUser();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource()
            .withProject(projectId)
            .withOrganisation(organisationId)
            .build();

        assertFalse(rules.internalUsersCanReadPendingPartnerProgress(partnerOrg, user));
    }

    @Test
    public void externalFinanceUsersCanViewPendingPartnerProgress() {

        long projectId = 1L;
        long organisationId = 2L;
        Competition competition = newCompetition().build();
        UserResource user = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();
        Project project = newProject().withId(projectId).withApplication(newApplication()
                .withCompetition(competition).build()).build();
        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withOrganisation(organisationId).withProject(project.getId()).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUsersCanReadPendingPartnerProgress(partnerOrg, user));
    }

    @Test
    public void partnersCanUpdateTheirOwnPendingPartnerProgress() {
        long projectId = 1L;
        long organisationId = 2L;
        UserResource user = newUserResource().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource()
            .withProject(projectId)
            .withOrganisation(organisationId)
            .build();
        ProjectUser projectUser = newProjectUser()
            .build();
        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(projectId, user.getId(), organisationId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(projectUser);

        assertTrue(rules.partnersCanUpdateTheirOwnPendingPartnerProgress(partnerOrg, user));
    }

    @Test
    public void internalUsersCanRemovePartnerOrganisations() {
        long projectId = 1L;
        long organisationId = 2L;

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource()
                .withProject(projectId)
                .withOrganisation(organisationId)
                .build();

        allGlobalRoleUsers.forEach(user -> {
            if (isInternalAdmin(user)) {
                assertTrue(rules.internalUsersCanRemovePartnerOrganisations(partnerOrg, user));
            } else {
                assertFalse(rules.internalUsersCanRemovePartnerOrganisations(partnerOrg, user));
            }
        });
    }
}
