package com.worth.ifs.application.transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.application.builder.ApplicationStatusBuilder.newApplicationStatus;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ApplicationServiceTest extends BaseUnitTestMocksTest {

    private final Log log = LogFactory.getLog(getClass());

    @InjectMocks
    private ApplicationService applicationService = new ApplicationServiceImpl();


    @Test
    public void applicationServiceShouldReturnApplicationByUserId() throws Exception {
        User testUser2 = new User(2L, "testUser2",  "email2@email.nl", "password", "test/image/url/2", "testToken456def", null);
        User testUser1 = new User(1L, "testUser1",  "email1@email.nl", "password", "test/image/url/1", "testToken123abc", null);

        Application testApplication1 = new Application(null, "testApplication1Name", null, null, 1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null, null, 2L);
        Application testApplication3 = new Application(null, "testApplication3Name", null, null, 3L);

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

        List<Application> applicationsForUser1 = applicationService.findByUserId(testUser1.getId());
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication1.getId(), applicationsForUser1.get(0).getId());
        assertEquals(testApplication2.getId(), applicationsForUser1.get(1).getId());

        List<Application> applicationsForUser2 = applicationService.findByUserId(testUser2.getId());
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication2.getId(), applicationsForUser2.get(0).getId());
        assertEquals(testApplication3.getId(), applicationsForUser2.get(1).getId());
    }


    @Test
    public void applicationControllerCanCreateApplication() throws Exception {
        Long competitionId = 1L;
        Long organisationId = 2L;
        Long userId = 3L;
        Long processRoleId = 4L;
        String roleName = UserRoleType.LEADAPPLICANT.getName();
        Competition competition = CompetitionBuilder.newCompetition().with(id(1L)).build();
        Role role = newRole().with(name(roleName)).build();
        Organisation organisation = newOrganisation().with(id(organisationId)).build();
        User user = newUser().with(id(userId)).withOrganisations(organisation).build();
        ApplicationStatus applicationStatus = newApplicationStatus().withName(ApplicationStatusConstants.CREATED.getName()).build();

        String applicationName = "testApplication";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode applicationNameNode = mapper.createObjectNode().put("name", applicationName);

        when(applicationStatusRepositoryMock.findByName(applicationStatus.getName())).thenReturn(Arrays.asList(applicationStatus));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(roleRepositoryMock.findByName(role.getName())).thenReturn(Arrays.asList(role));
        when(userRepositoryMock.findOne(userId)).thenReturn(user);

        Application newApplication = applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, applicationNameNode);
        assertEquals(applicationName, newApplication.getName());
        assertEquals(applicationStatus, newApplication.getApplicationStatus());
        assertEquals(competitionId, newApplication.getCompetition().getId());
    }
}