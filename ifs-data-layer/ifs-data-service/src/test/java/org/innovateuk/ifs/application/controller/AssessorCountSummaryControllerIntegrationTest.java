package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class AssessorCountSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<AssessorCountSummaryController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(AssessorCountSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void getAssessorCountSummariesByCompetitionId() {
        long competitionId = 1L;
        loginCompAdmin();

        Competition competition = competitionRepository.findById(competitionId).get();

        List<Profile> profiles = newProfile().with(id(null)).withSkillsAreas("Java Development").build(2);
        profileRepository.saveAll(profiles);

        List<User> users = newUser()
                .with(id(null))
                .withFirstName("Tom", "Cari")
                .withLastName("Baldwin", "Morton")
                .withProfileId(profiles.stream().map(Profile::getId).toArray(Long[]::new))
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8")
                .build(2);
        userRepository.saveAll(users);

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .with(id(null))
                .withUser(users.toArray(new User[users.size()]))
                .withCompetition(competition)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build(2);
        assessmentParticipantRepository.saveAll(competitionParticipants);

        Application application = newApplication().withCompetition(competition).with(id(null)).build();
        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withRole(Role.ASSESSOR)
                .withApplication(application)
                .withUser(users.get(0))
                .build();

        processRoleRepository.save(processRole);

        Assessment assessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(processRole)
                .withProcessState(AssessmentState.PENDING)
                .build();

        assessmentRepository.save(assessment);

        AssessorCountSummaryPageResource counts = controller.getAssessorCountSummariesByCompetitionId(competitionId, Optional.empty(), Optional.empty(), 0,3).getSuccess();

        assertEquals(2, counts.getTotalElements());
        assertEquals(0, counts.getNumber());
        assertEquals(1, counts.getTotalPages());
        assertEquals(2, counts.getContent().size());
    }
}