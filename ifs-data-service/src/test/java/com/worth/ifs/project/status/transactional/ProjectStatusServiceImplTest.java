package com.worth.ifs.project.status.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.transactional.ProjectStatusService;
import com.worth.ifs.project.transactional.ProjectStatusServiceImpl;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

public class ProjectStatusServiceImplTest extends BaseServiceUnitTest<ProjectStatusService> {

    @Override
    protected ProjectStatusService supplyServiceUnderTest() {
        return new ProjectStatusServiceImpl();
    }

    @Test
    public void testGetCompetitionStatus(){
        Long competitionId = 123L;
        Competition competition = newCompetition().withId(competitionId).build();
        Organisation organisation = newOrganisation().build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        User user = newUser().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisation(organisation).build();
        List<Application> applications = newApplication().withCompetition(competition).withProcessRoles(leadApplicantProcessRole).build(3);
        List<Project> projects = newProject().withApplication(applications.get(0), applications.get(1), applications.get(2)).build(3);

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(projects);

        ServiceResult<CompetitionProjectsStatusResource> result = service.getCompetitionStatus(competitionId);

        assertTrue(result.isSuccess());
    }
}
