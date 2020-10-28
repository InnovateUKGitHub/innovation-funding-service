package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterDecisionResource;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.repository.SimpleOrganisationRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.SimpleOrganisationBuilder.newSimpleOrganisation;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertThat;

@Rollback
@Transactional
    public class SupporterAssignmentServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private SupporterAssignmentService supporterAssignmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private SimpleOrganisationRepository simpleOrganisationRepository;

    @Test
    public void findApplicationsNeedingSupporters() {
        setLoggedInUser(getIfsAdmin());
        TestData data = setupTestData();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        ApplicationsForCofundingPageResource resource = supporterAssignmentService.findApplicationsNeedingSupporters(data.competition.getId(), "", pageRequest).getSuccess();

        assertThat(resource.getTotalElements(), equalTo(2L));
        assertThat(resource.getContent().get(0).getLead(), equalTo("lead org"));
        assertThat(resource.getContent().get(0).getName(), equalTo("App name 1"));
        assertThat(resource.getContent().get(0).getAccepted(), equalTo(1L));
        assertThat(resource.getContent().get(0).getRejected(), equalTo(1L));
        assertThat(resource.getContent().get(0).getAssigned(), equalTo(1L));
        assertThat(resource.getContent().get(0).getTotal(), equalTo(3L));
        assertThat(resource.getContent().get(1).getName(), equalTo("App name 2"));
        assertThat(resource.getContent().get(1).getAccepted(), equalTo(0L));
        assertThat(resource.getContent().get(1).getRejected(), equalTo(0L));
        assertThat(resource.getContent().get(1).getAssigned(), equalTo(0L));
        assertThat(resource.getContent().get(1).getTotal(), equalTo(0L));

        ApplicationsForCofundingPageResource filterResponse = supporterAssignmentService.findApplicationsNeedingSupporters(data.competition.getId(), String.valueOf(data.application1.getId()), pageRequest).getSuccess();

        assertThat(filterResponse.getTotalElements(), equalTo(1L));
        assertThat(filterResponse.getContent().get(0).getName(), equalTo("App name 1"));
    }

    @Test
    public void findAvailableSupportersForApplication() {
        setLoggedInUser(getIfsAdmin());
        TestData data = setupTestData();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("firstName"));

        SupportersAvailableForApplicationPageResource resource = supporterAssignmentService.findAvailableSupportersForApplication(data.application1.getId(), "", pageRequest).getSuccess();
        assertThat(resource.getTotalElements(), equalTo(1L));
        assertThat(resource.getContent().get(0).getName(), equalTo("Bob unassigned"));
        assertThat(resource.getContent().get(0).getOrganisation(), equalTo("Simple 1"));
        assertThat(resource.getContent().get(0).getEmail(), equalTo("supporter1@gmail.com"));
    }

    private TestData setupTestData() {
        Competition competition = competitionRepository.save(newCompetition().withId(null).build());
        Application application = applicationRepository.save(newApplication().withName("App name 1").withId(null).withCompetition(competition).withApplicationState(ApplicationState.SUBMITTED).build());
        Application application2 = applicationRepository.save(newApplication().withName("App name 2").withId(null).withCompetition(competition).withApplicationState(ApplicationState.SUBMITTED).build());

        Organisation lead = organisationRepository.save(newOrganisation().withId(null).withName("lead org").build());
        processRoleRepository.saveAll(
                newProcessRole().withApplication(application, application2)
                        .withOrganisationId(lead.getId())
                        .withUser(userRepository.save(newUser().withId(null).withEmailAddress("asd@gmail").withUid("asdasd").build()))
                        .withRole(Role.LEADAPPLICANT)
                        .build(2)
        );

        List<Profile> profiles = newProfile()
                .withId(null)
                .withSimpleOrganisation(
                        simpleOrganisationRepository.save(newSimpleOrganisation().withName("Simple 1").build()),
                        simpleOrganisationRepository.save(newSimpleOrganisation().withName("Simple 2").build()),
                        simpleOrganisationRepository.save(newSimpleOrganisation().withName("Simple 3").build()),
                        simpleOrganisationRepository.save(newSimpleOrganisation().withName("Simple 4").build())
                ).build(4);
        profiles = newArrayList(profileRepository.saveAll(profiles));

        List<User> supporters = newUser()
                .withId(null)
                .withUid("1", "2", "3", "4")
                .withEmailAddress("supporter1@gmail.com", "supporter2@gmail.com", "supporter3@gmail.com", "supporter4@gmail.com")
                .withFirstName("Bob", "Frank", "Jim", "Rob")
                .withLastName("unassigned", "assigned", "accepted", "rejected")
                .withProfileId(profiles.get(0).getId(), profiles.get(1).getId(), profiles.get(2).getId(), profiles.get(3).getId())
                .withRoles(newHashSet(Role.SUPPORTER))
                .withCreatedBy()
                .build(4);

        supporters = newArrayList(userRepository.saveAll(supporters));

        flushAndClearSession();

        supporterAssignmentService.assign(supporters.get(1).getId(), application.getId()).getSuccess();
        SupporterAssignmentResource toAccept = supporterAssignmentService.assign(supporters.get(2).getId(), application.getId()).getSuccess();
        SupporterAssignmentResource toReject = supporterAssignmentService.assign(supporters.get(3).getId(), application.getId()).getSuccess();

        SupporterDecisionResource acceptDecision = new SupporterDecisionResource();
        acceptDecision.setAccept(true);
        acceptDecision.setComments("Amazing");
        supporterAssignmentService.decision(toAccept.getAssignmentId(), acceptDecision).getSuccess();

        SupporterDecisionResource rejectDecision = new SupporterDecisionResource();
        acceptDecision.setAccept(false);
        acceptDecision.setComments("Terrible");
        supporterAssignmentService.decision(toReject.getAssignmentId(), rejectDecision).getSuccess();

        return new TestData(competition, application, application2);
    }

    private class TestData {
        Competition competition;
        Application application1;
        Application application2;

        public TestData(Competition competition, Application application1, Application application2) {
            this.competition = competition;
            this.application1 = application1;
            this.application2 = application2;
        }
    }
}
