package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.supporter.domain.builder.SupporterAssignmentBuilder.newSupporterAssignment;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertThat;

@Rollback
@Transactional
    public class SupporterDashboardServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private SupporterDashboardService supporterDashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    public void findApplicationsNeedingSupporters() {
        loginSteveSmith();
        TestData data = setupTestData();
        setLoggedInUser(newUserResource().withRoleGlobal(Role.SUPPORTER).withId(data.supporter.getId()).build());

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("activityState"));

        SupporterDashboardApplicationPageResource result = supporterDashboardService.getApplicationsForCofunding(data.supporter.getId(), data.competition.getId(), pageRequest).getSuccess();

        assertThat(result.getTotalElements(), equalTo(3L));
        assertThat(result.getContent().get(0).getName(), equalTo("3"));
        assertThat(result.getContent().get(0).getLead(), equalTo("3"));
        assertThat(result.getContent().get(0).getState(), equalTo(SupporterState.CREATED));

        assertThat(result.getContent().get(1).getName(), equalTo("2"));
        assertThat(result.getContent().get(1).getLead(), equalTo("2"));
        assertThat(result.getContent().get(1).getState(), equalTo(SupporterState.ACCEPTED));

        assertThat(result.getContent().get(2).getName(), equalTo("1"));
        assertThat(result.getContent().get(2).getLead(), equalTo("1"));
        assertThat(result.getContent().get(2).getState(), equalTo(SupporterState.REJECTED));
    }


    private TestData setupTestData() {
        Competition competition = competitionRepository.save(newCompetition().withId(null).build());
        List<Application> applications =
                newArrayList(applicationRepository.saveAll(newApplication().withName("1", "2", "3").withId(null).withCompetition(competition).build(3)));
        List<Organisation> organisations =
                newArrayList(organisationRepository.saveAll(newOrganisation().withName("1", "2", "3").withId(null).build(3)));

        processRoleRepository.saveAll(
                newProcessRole().withApplication(toArray(applications, Application.class))
                        .withOrganisation(toArray(organisations, Organisation.class))
                        .withUser(userRepository.save(newUser().withId(null).withEmailAddress("asd@gmail").withUid("asdasd").build()))
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .build(3)
        );

        User supporter = userRepository.save(newUser()
                .withId(null)
                .withUid("1")
                .withEmailAddress("supporter1@gmail.com")
                .withFirstName("Bob")
                .withLastName("Bobberson")
                .withRoles(newHashSet(Role.SUPPORTER))
                .build());

        supporterAssignmentRepository.saveAll(newSupporterAssignment()
                .withParticipant(supporter)
                .withApplication(toArray(applications, Application.class))
                .withProcessState(SupporterState.REJECTED, SupporterState.ACCEPTED, SupporterState.CREATED)
                .build(3));

        flushAndClearSession();

        return new TestData(competition, applications, supporter);
    }

    private class TestData {
        Competition competition;
        List<Application> applications;
        User supporter;

        public TestData(Competition competition, List<Application> applications, User supporter) {
            this.competition = competition;
            this.applications = applications;
            this.supporter = supporter;
        }
    }
}
