package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ApplicationFinancePermissionRulesTest extends BasePermissionRulesTest<ApplicationFinancePermissionRules> {

    private ApplicationFinanceResource applicationFinance;
    private Application application;
    private UserResource assessor;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;
    private UserResource stakeholderResource;

    private ApplicationFinanceResource otherApplicationFinance;
    private UserResource otherLeadApplicant;

    @Override
    protected ApplicationFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationFinancePermissionRules();
    }

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Before
    public void setup() {

        // Create a compAdmin
        compAdmin = this.compAdminUser();

        {
            // Set up users on an organisation and application
            final long applicationId = 1L;
            final long organisationId = 2L;

            Competition competition = newCompetition()
                    .withAssessorFinanceView(AssessorFinanceView.DETAILED).build();
            application = newApplication().with(id(applicationId)).withCompetition(competition).build();

            OrganisationResource organisation = newOrganisationResource().with(id(organisationId)).build();
            applicationFinance = newApplicationFinanceResource().withOrganisation(organisation.getId()).withApplication(application.getId()).build();
            leadApplicant = newUserResource().build();
            assessor = newUserResource().build();
            collaborator = newUserResource().build();
            stakeholderResource = newUserResource().withRoleGlobal(STAKEHOLDER).build();

            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicant.getId(), Role.LEADAPPLICANT, applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicant.getId(), Role.LEADAPPLICANT, applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaborator.getId(), Role.COLLABORATOR, applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(assessor.getId(), Role.ASSESSOR, applicationId, organisationId)).thenReturn(newProcessRole().build());

            ProcessRole compAdminProcessRole = newProcessRole().withRole(Role.COMP_ADMIN).build();

            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationId, Role.LEADAPPLICANT)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationId, Role.COLLABORATOR)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(assessor.getId(), applicationId, Role.ASSESSOR)).thenReturn(true);
            when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(compAdmin.getId(), applicantProcessRoles(), applicationId)).thenReturn(compAdminProcessRole);

            when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
            when(competitionRepository.findById(application.getCompetition().getId())).thenReturn(Optional.of(competition));
        }
        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3L;
            final long otherOrganisationId = 4L;
            OrganisationResource otherOrganisation = newOrganisationResource().with(id(otherOrganisationId)).build();
            Competition otherCompetition = newCompetition().withAssessorFinanceView(AssessorFinanceView.DETAILED).build();
            Application otherApplication = newApplication().with(id(otherApplicationId)).withCompetition(otherCompetition).build();
            otherApplicationFinance = newApplicationFinanceResource().withOrganisation(otherOrganisation.getId()).withApplication(otherApplication.getId()).build();
            otherLeadApplicant = newUserResource().build();
            when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(), Role.LEADAPPLICANT, otherApplicationId, otherOrganisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(otherLeadApplicant.getId(), otherApplicationId, Role.LEADAPPLICANT)).thenReturn(true);
        }
    }

    @Test
    public void consortiumCanSeeTheApplicationFinancesForTheirOrganisation() {
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
        allGlobalRoleUsers.forEach(user ->
                allGlobalRoleUsers.forEach(otherUser -> {
                    if (allInternalUsers.contains(user)) {
                        assertTrue(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, user));
                    } else {
                        assertFalse(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, user));
                    }
                }));
    }

    @Test
    public void stakeholdersCanSeeApplicationFinancesForOrganisations() {

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withApplication(application.getId())
                .build();

        when(applicationRepository.findById(applicationFinanceResource.getApplication())).thenReturn(Optional.of(application));
        when(stakeholderRepository.existsByCompetitionIdAndUserId(application.getCompetition().getId(), stakeholderResource.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanSeeApplicationFinancesForOrganisations(applicationFinanceResource, stakeholderResource));
        allInternalUsers.forEach(user ->
                assertFalse(rules.stakeholdersCanSeeApplicationFinancesForOrganisations(applicationFinanceResource, user)));
    }

    @Test
    public void updateCosts() {
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, collaborator));

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, compAdmin));
    }

    @Test
    public void addCosts() {
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, collaborator));

        assertTrue(rules.internalUserCanAddACostToApplicationFinance(applicationFinance, supportUser()));
        assertTrue(rules.internalUserCanAddACostToApplicationFinance(applicationFinance, innovationLeadUser()));
        assertTrue(rules.internalUserCanAddACostToApplicationFinance(applicationFinance, ifsAdminUser()));
        assertTrue(rules.internalUserCanAddACostToApplicationFinance(applicationFinance, compAdminUser()));
        assertTrue(rules.internalUserCanAddACostToApplicationFinance(applicationFinance, projectFinanceUser()));

        assertTrue(rules.assessorCanAddACostToApplicationFinance(applicationFinance, assessor));

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, compAdmin));

        assertFalse(rules.internalUserCanAddACostToApplicationFinance(applicationFinance, systemRegistrationUser()));
    }

    @Test
    public void stakeholdersCanViewCostsToApplicationFinance() {

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withApplication(application.getId())
                .build();

        when(applicationRepository.findById(applicationFinanceResource.getApplication())).thenReturn(Optional.of(application));
        when(stakeholderRepository.existsByCompetitionIdAndUserId(application.getCompetition().getId(), stakeholderResource.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanAddACostToApplicationFinance(applicationFinanceResource, stakeholderResource));
        allInternalUsers.forEach(user -> assertFalse(rules.stakeholdersCanAddACostToApplicationFinance(applicationFinanceResource, user)));
    }

    @Test
    public void leadCanGetFileResourceForPartner() {
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, collaborator));
        assertTrue(rules.assessorUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, assessor));
        assertFalse(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(applicationFinance, compAdmin));
    }

    @Test
    public void internalUserCanGetFileResourceForPartner() {
        assertTrue(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, compAdmin));
        assertTrue(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, projectFinanceUser()));
        assertFalse(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, collaborator));
        assertFalse(rules.internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(applicationFinance, leadApplicant));
    }

    @Test
    public void consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation() {
        assertTrue(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation() {
        assertTrue(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation() {
        assertTrue(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void monitoringOfficersCanSeeApplicationFinancesForOrganisations() {
        Project project = newProject().build();
        when(projectRepository.findOneByApplicationId(anyLong())).thenReturn(project);
        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), monitoringOfficerUser().getId())).thenReturn(true);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(MONITORING_OFFICER)) {
                assertTrue(rules.monitoringOfficersCanSeeApplicationFinancesForOrganisations(applicationFinance, monitoringOfficerUser()));
            } else {
                Assert.assertFalse(rules.monitoringOfficersCanSeeApplicationFinancesForOrganisations(applicationFinance, user));
            }
        });
    }

    @Test
    public void internalUserCanGetApplicationFinance() {
        assertTrue(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, compAdmin));
        assertTrue(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, projectFinanceUser()));
        assertFalse(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, collaborator));
        assertFalse(rules.internalUserCanSeeApplicationFinancesForOrganisations(applicationFinance, leadApplicant));
    }
}
