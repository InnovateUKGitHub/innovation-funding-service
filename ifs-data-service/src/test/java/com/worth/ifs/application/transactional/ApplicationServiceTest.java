package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.resource.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.ApplicationStatusBuilder.newApplicationStatus;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

public class ApplicationServiceTest extends BaseUnitTestMocksTest {

    private final Log log = LogFactory.getLog(getClass());

    @InjectMocks
    private ApplicationService applicationService = new ApplicationServiceImpl();


    @Test
    public void applicationServiceShouldReturnApplicationByUserId() throws Exception {
        User testUser2 = new User(2L, "test", "User2",  "email2@email.nl", "testToken456def", null, "my-uid");
        User testUser1 = new User(1L, "test", "User1",  "email1@email.nl", "testToken123abc", null, "my-uid");

        Application testApplication1 = new Application(null, "testApplication1Name", null, null, 1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null, null, 2L);
        Application testApplication3 = new Application(null, "testApplication3Name", null, null, 3L);

        ApplicationResource testApplication1Resource = newApplicationResource().with(id(1L)).withName("testApplication1Name").build();
        ApplicationResource testApplication2Resource = newApplicationResource().with(id(2L)).withName("testApplication2Name").build();
        ApplicationResource testApplication3Resource = newApplicationResource().with(id(3L)).withName("testApplication3Name").build();

        Organisation organisation1 = new Organisation(1L, "test organisation 1");
        Organisation organisation2 = new Organisation(2L, "test organisation 2");

        ProcessRole testProcessRole1 = new ProcessRole(0L, testUser1, testApplication1, new Role(), organisation1);
        ProcessRole testProcessRole2 = new ProcessRole(1L, testUser1, testApplication2, new Role(), organisation1);
        ProcessRole testProcessRole3 = new ProcessRole(2L, testUser2, testApplication2, new Role(), organisation2);
        ProcessRole testProcessRole4 = new ProcessRole(3L, testUser2, testApplication3, new Role(), organisation2);

        when(userRepositoryMock.findOne(1L)).thenReturn(testUser1);
        when(userRepositoryMock.findOne(2L)).thenReturn(testUser2);

        when(processRoleRepositoryMock.findByUser(testUser1)).thenReturn(new ArrayList<ProcessRole>() {{
            add(testProcessRole1);
            add(testProcessRole2);
        }});

        when(processRoleRepositoryMock.findByUser(testUser2)).thenReturn(new ArrayList<ProcessRole>() {{
            add(testProcessRole3);
            add(testProcessRole4);
        }});

        when(applicationMapperMock.mapToResource(testApplication1)).thenReturn(testApplication1Resource);
        when(applicationMapperMock.mapToResource(testApplication2)).thenReturn(testApplication2Resource);
        when(applicationMapperMock.mapToResource(testApplication3)).thenReturn(testApplication3Resource);

        List<ApplicationResource> applicationsForUser1 = applicationService.findByUserId(testUser1.getId()).getSuccessObject();
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication1Resource.getId(), applicationsForUser1.get(0).getId());
        assertEquals(testApplication2Resource.getId(), applicationsForUser1.get(1).getId());

        List<ApplicationResource> applicationsForUser2 = applicationService.findByUserId(testUser2.getId()).getSuccessObject();
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication2Resource.getId(), applicationsForUser2.get(0).getId());
        assertEquals(testApplication3Resource.getId(), applicationsForUser2.get(1).getId());
    }

    @Test
    public void applicationControllerCanCreateApplication() throws Exception {
        Long competitionId = 1L;
        Long organisationId = 2L;
        Long userId = 3L;
        String roleName = UserRoleType.LEADAPPLICANT.getName();
        Competition competition = CompetitionBuilder.newCompetition().with(id(1L)).build();
        Role role = newRole().with(name(roleName)).build();
        Organisation organisation = newOrganisation().with(id(organisationId)).build();
        User user = newUser().with(id(userId)).withOrganisations(organisation).build();
        ApplicationStatus applicationStatus = newApplicationStatus().withName(ApplicationStatusConstants.CREATED.getName()).build();

        String applicationName = "testApplication";

        ApplicationResource newApplication = newApplicationResource().build();

        when(applicationStatusRepositoryMock.findByName(applicationStatus.getName())).thenReturn(Arrays.asList(applicationStatus));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(roleRepositoryMock.findByName(role.getName())).thenReturn(Arrays.asList(role));
        when(userRepositoryMock.findOne(userId)).thenReturn(user);

        Application applicationExpectations = argThat(lambdaMatches(created -> {
            assertEquals(applicationName, created.getName());
            assertEquals(applicationStatus, created.getApplicationStatus());
            assertEquals(competitionId, created.getCompetition().getId());
            return true;
        }));

        when(applicationRepositoryMock.save(applicationExpectations)).thenReturn(applicationExpectations);
        when(applicationMapperMock.mapToResource(applicationExpectations)).thenReturn(newApplication);

        ApplicationResource created = applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, applicationName).getSuccessObject();
        assertEquals(newApplication, created);
    }
}