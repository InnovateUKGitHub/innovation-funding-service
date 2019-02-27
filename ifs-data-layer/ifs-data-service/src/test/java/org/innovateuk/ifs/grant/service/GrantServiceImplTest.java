package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * Tests around the {@link GrantServiceImpl}.
 */
public class GrantServiceImplTest extends BaseServiceUnitTest<GrantServiceImpl> {
    private static final long APPLICATION_ID = 9L;

    @Mock
    private GrantEndpoint grantEndpoint;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GrantProcessService grantProcessService;

    @Mock
    protected GrantMapper grantMapper;

    @Override
    protected GrantServiceImpl supplyServiceUnderTest() {
        return new GrantServiceImpl();
    }

    @Test
    public void sendReadyProjects() {

        ReflectionTestUtils.setField(service, "allocateLiveProjectsRole", true);

        User user = newUser()
                .withFirstName("A")
                .withLastName("B")
                .withEmailAddress("a@b.com")
                .build();

        setLoggedInUser(newUserResource()
                .withId(user.getId())
                .build());

        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        ProjectUser projectManager = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withUser(user)
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg1 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().build())
                .withOrganisation(organisation1)
                .build();

        ProjectUser normalPartnerOrg1 = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(newUser().build())
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg2 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().build())
                .withOrganisation(organisation2)
                .build();

        ProjectUser normalPartnerOrg2 = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(newUser().build())
                .withOrganisation(organisation2)
                .build();

        List<ProjectUser> projectUsers =
                asList(projectManager, financeContactOrg1, normalPartnerOrg1, financeContactOrg2, normalPartnerOrg2);


        Project project = newProject()
                .withId(1L)
                .withDuration(12L)
                .withProjectUsers(projectUsers)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(organisation1, organisation2)
                        .build(2)
                )
                .withApplication(
                        newApplication()
                                .withId(APPLICATION_ID)
                                .withCompetition(
                                        newCompetition().withId(2L).build())
                                .build()).build();
        GrantProcess process = new GrantProcess(APPLICATION_ID);
        Grant grant = new Grant().id(APPLICATION_ID);

        when(projectRepository.findOneByApplicationId(APPLICATION_ID)).thenReturn(project);
        when(grantMapper.mapToGrant(project)).thenReturn(grant);
        when(grantEndpoint.send(grant)).thenReturn(serviceSuccess());
        when(grantProcessService.findReadyToSend()).thenReturn(singletonList(process));

        ServiceResult<Void> result = service.sendReadyProjects();

        assertThat(result.isSuccess(), equalTo(true));

        verify(grantEndpoint, only()).send(createLambdaMatcher(matchGrant(project)));

        // assert that the Project Manager and the Finance Contacts for each Partner Organisation are granted access to
        // Live Projects
        assertTrue(projectManager.getUser().hasRole(Role.LIVE_PROJECTS_USER));
        assertTrue(financeContactOrg1.getUser().hasRole(Role.LIVE_PROJECTS_USER));
        assertTrue(financeContactOrg2.getUser().hasRole(Role.LIVE_PROJECTS_USER));

        // assert that "normal" Partner users are NOT granted access to Live Projects
        assertFalse(normalPartnerOrg1.getUser().hasRole(Role.LIVE_PROJECTS_USER));
        assertFalse(normalPartnerOrg2.getUser().hasRole(Role.LIVE_PROJECTS_USER));
    }

    private static Predicate<Grant> matchGrant(Project project) {
        return grant -> {
            assertThat(grant.getId(), equalTo(project.getApplication().getId()));
            return true;
        };
    }
}
