package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.time.LocalDate;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.application.builder.ApplicationStatusBuilder.newApplicationStatus;
import static com.worth.ifs.application.constant.ApplicationStatusConstants.CREATED;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ApplicationServiceImpl}
 */
public class ApplicationServiceImplMockTest extends BaseServiceMocksTest<ApplicationService> {

    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Test
    public void test_createApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisation(organisation).build();
        ApplicationStatus applicationStatus = newApplicationStatus().withName(CREATED).build();

        when(applicationStatusRepositoryMock.findByName(CREATED.getName())).thenReturn(asList(applicationStatus));
        when(competitionsRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(roleRepositoryMock.findByName(leadApplicantRole.getName())).thenReturn(asList(leadApplicantRole));
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        Application created =
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("testApplication", competition.getId(), user.getId());

        verify(applicationRepositoryMock).save(isA(Application.class));
        verify(processRoleRepositoryMock).save(isA(ProcessRole.class));

        assertEquals("testApplication", created.getName());
        assertEquals(applicationStatus.getId(), created.getApplicationStatus().getId());
        assertEquals(Long.valueOf(3), created.getDurationInMonths());
        assertEquals(competition.getId(), created.getCompetition().getId());
        assertEquals(LocalDate.now(), created.getStartDate());

        assertEquals(1, created.getProcessRoles().size());
        ProcessRole createdProcessRole = created.getProcessRoles().get(0);
        assertNull(createdProcessRole.getId());
        assertNull(createdProcessRole.getApplication().getId());
        assertEquals(organisation.getId(), createdProcessRole.getOrganisation().getId());
        assertEquals(leadApplicantRole.getId(), createdProcessRole.getRole().getId());
        assertEquals(user.getId(), createdProcessRole.getUser().getId());
    }
}
