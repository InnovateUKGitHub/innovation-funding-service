package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isIn;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.junit.Assert.*;

public class AssessorCompetitionSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<AssessorCompetitionSummaryController> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(AssessorCompetitionSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void getAssessorSummary() {
        loginCompAdmin();

        Competition competition = newCompetition()
                .withId()
                .withName("Test Competition")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        competitionRepository.save(competition);

        List<Application> applications = newApplication()
                .withId()
                .withName("Test Application 1", "Test Application 2", "Test Application 3")
                .withCompetition(competition)
                .build(3);

        applicationRepository.saveAll(applications);

        User paulPlum = userRepository.findByEmail("paul.plum@gmail.com").orElse(null);
        User felixWilson = userRepository.findByEmail("felix.wilson@gmail.com").orElse(null);

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withId()
                .withName("Metallurgy", "Alchemy", "Forgemastery")
                .build(3);
        
        innovationAreaRepository.saveAll(innovationAreas);

        Profile profile = newProfile()
                .withId()
                .withBusinessType(ACADEMIC)
                .withInnovationAreas(innovationAreas)
                .build();

        profileRepository.save(profile);

        paulPlum.setProfileId(profile.getId());

        userRepository.save(paulPlum);

        List<ProcessRole> processRoles = newProcessRole()
                .withId()
                .withRole(ProcessRoleType.ASSESSOR)
                .withApplication(applications.get(0), applications.get(1), applications.get(2), applications.get(0), applications.get(1))
                .withUser(paulPlum, paulPlum, paulPlum, felixWilson, felixWilson)
                .build(5);

        User steveSmith = userRepository.findByEmail("steve.smith@empire.com").orElse(null);
        List<Organisation> organisations = newOrganisation()
                .withId()
                .withName("Test Org 1", "Test Org 2", "Test 3")
                .build(3);

        organisationRepository.saveAll(organisations);

        processRoles.addAll(
                newProcessRole()
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .withApplication(applications.get(0), applications.get(1), applications.get(2))
                        .withUser(steveSmith)
                        .withOrganisationId(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId())
                        .build(3)
        );

        processRoleRepository.saveAll(processRoles);

        AssessmentRejectOutcome rejectOutcome = newAssessmentRejectOutcome()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment("rejection comment")
                .build();

        List<Assessment> assessments = newAssessment()
                .withId()
                .withApplication(applications.get(0), applications.get(1), applications.get(2), applications.get(0), applications.get(1))
                .withParticipant(processRoles.get(0), processRoles.get(1), processRoles.get(2), processRoles.get(3), processRoles.get(4))
                .withProcessState(REJECTED, SUBMITTED, ACCEPTED, SUBMITTED, SUBMITTED)
                .withRejection(rejectOutcome, null, null, null, null)
                .build(5);

        rejectOutcome.setProcess(assessments.get(0));

        assessorFormInputResponseRepository.deleteAll();
        assessmentRepository.deleteAll();
        assessmentRepository.saveAll(assessments);

        flushAndClearSession();

        RestResult<AssessorCompetitionSummaryResource> result = this.controller.getAssessorSummary(paulPlum.getId(), competition.getId());

        assertTrue(result.isSuccess());

        AssessorCompetitionSummaryResource summaryResource = result.getSuccess();

        assertEquals(competition.getId().longValue(), summaryResource.getCompetitionId());
        assertEquals(competition.getName(), summaryResource.getCompetitionName());
        assertEquals(competition.getCompetitionStatus(), summaryResource.getCompetitionStatus());
        assertEquals(2, summaryResource.getTotalApplications());
        assertEquals(getPaulPlum(), summaryResource.getAssessor().getUser());
        assertEquals(ACADEMIC, summaryResource.getAssessor().getProfile().getBusinessType());

        List<InnovationAreaResource> innovationAreaResources = summaryResource.getAssessor().getProfile().getInnovationAreas();
        assertEquals(3, innovationAreaResources.size());

        List<String> expectedInnovationAreaNames = asList("Metallurgy", "Alchemy", "Forgemastery");
        assertThat(innovationAreas.get(0).getName(), isIn(expectedInnovationAreaNames));
        assertThat(innovationAreas.get(1).getName(), isIn(expectedInnovationAreaNames));
        assertThat(innovationAreas.get(2).getName(), isIn(expectedInnovationAreaNames));

        assertEquals(3, summaryResource.getAssignedAssessments().size());

        AssessorAssessmentResource[] expectedAssessorAssessmentResources = newAssessorAssessmentResource()
                .withApplicationId(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId())
                .withApplicationName(applications.get(0).getName(), applications.get(1).getName(), applications.get(2).getName())
                .withLeadOrganisation(organisations.get(0).getName(), organisations.get(1).getName(), organisations.get(2).getName())
                .withTotalAssessors(1, 2, 1)
                .withState(REJECTED, SUBMITTED, ACCEPTED)
                .withRejectionReason(rejectOutcome.getRejectReason(), null, null)
                .withRejectionComment(rejectOutcome.getRejectComment(), null, null)
                .withAssessmentId(assessments.get(0).getId(), assessments.get(1).getId(), assessments.get(2).getId())
                .buildArray(3, AssessorAssessmentResource.class);

        assertThat(summaryResource.getAssignedAssessments(), hasItems(expectedAssessorAssessmentResources));
    }
}