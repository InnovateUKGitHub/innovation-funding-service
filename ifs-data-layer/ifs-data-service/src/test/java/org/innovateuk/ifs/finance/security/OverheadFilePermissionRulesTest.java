package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OverheadFilePermissionRulesTest extends BasePermissionRulesTest<OverheadFilePermissionRules> {

    private ApplicationFinanceRow overheads;
    private ApplicationFinanceRow submittedOverheads;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;
    private UserResource otherLeadApplicant;
    private UserResource supportUser;
    private UserResource ifsAdmin;
    private UserResource projectFinance;
    private UserResource innovationLead;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Override
    protected OverheadFilePermissionRules supplyPermissionRulesUnderTest() {
        return new OverheadFilePermissionRules();
    }

    @Before
    public void setup() {

        // Create a compAdmin
        compAdmin = compAdminUser();
        {
            // Set up users on an organisation and application
            final long applicationId = 1L;
            final Long submittedApplicationId = 2L;
            final long organisationId = 2L;

            final Application application = newApplication()
                    .with(id(applicationId))
                    .withApplicationState(ApplicationState.OPENED)
                    .build();

            final Application submittedApplication = newApplication()
                    .with(id(submittedApplicationId))
                    .withApplicationState(ApplicationState.SUBMITTED)
                    .build();

            final Organisation organisation = newOrganisation().with(id(organisationId)).build();

            final ApplicationFinance applicationFinance = newApplicationFinance().withApplication(application).withOrganisation(organisation).build();
            final ApplicationFinance submittedApplicationFinance = newApplicationFinance().withApplication(submittedApplication).withOrganisation(organisation).build();

            overheads = newApplicationFinanceRow().withOwningFinance(applicationFinance).build();

            submittedOverheads = newApplicationFinanceRow().withOwningFinance(submittedApplicationFinance).build();

            leadApplicant = newUserResource().build();
            collaborator = newUserResource().build();
            when(applicationFinanceRowRepository.findById(overheads.getId())).thenReturn(Optional.of(overheads));
            when(applicationFinanceRowRepository.findById(submittedOverheads.getId())).thenReturn(Optional.of(submittedOverheads));
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicant.getId(), LEADAPPLICANT, applicationId, organisationId)).
                    thenReturn(newProcessRole().build());
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaborator.getId(), COLLABORATOR, applicationId, organisationId)).
                    thenReturn(newProcessRole().build());

            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
            when(applicationRepository.findById(submittedApplicationId)).thenReturn(Optional.of(submittedApplication));
        }

        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3L;
            final long otherOrganisationId = 4L;
            otherLeadApplicant = newUserResource().build();
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(),
                    LEADAPPLICANT, otherApplicationId, otherOrganisationId)).thenReturn(newProcessRole().build());
        }

        // Create project with users for testing getting of partner funding status
        {
            final long projectId = 1L;
            final long userId = 1L;
            when(projectUserRepository.findByProjectIdAndUserIdAndRole(projectId, userId, PROJECT_PARTNER)).
                    thenReturn(singletonList(newProjectUser().withId(userId).build()));
        }

        // Create different users with different project
        {
            final long otherProjectProjectId = 2L;
            final long otherProjectUserId = 2L;
            when(projectUserRepository.findByProjectIdAndUserIdAndRole(otherProjectProjectId, otherProjectUserId, PROJECT_PARTNER))
                    .thenReturn(singletonList(newProjectUser().withId(otherProjectUserId).build()));
        }
        // Create users with roles
        {
            ifsAdmin = newUserResource().withRolesGlobal(singletonList(IFS_ADMINISTRATOR)).build();
            supportUser = newUserResource().withRolesGlobal(singletonList(SUPPORT)).build();
            innovationLead = newUserResource().withRolesGlobal(singletonList(INNOVATION_LEAD)).build();
            projectFinance = newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build();
        }
    }

    @Test
    public void consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void supportAndIfsAdminUsersCanReadContentsOfAnOverheadsFileForAnApplication() {
        assertTrue(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, supportUser));
        assertTrue(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, ifsAdmin));

        assertFalse(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, projectFinance));
        assertFalse(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, innovationLead));
        assertFalse(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, compAdmin));
    }

    @Test
    public void compAdminAndInnovationLeadAndProjectFinanceUsersCanReadDetailsOfAnOverheadsFileForASubmittedApplication() {
        assertTrue(rules.internalUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, projectFinance));
        assertTrue(rules.internalUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, compAdmin));
        assertTrue(rules.internalUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, innovationLead));
        assertTrue(rules.internalUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, supportUser));
        assertTrue(rules.internalUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, ifsAdmin));
    }
}
