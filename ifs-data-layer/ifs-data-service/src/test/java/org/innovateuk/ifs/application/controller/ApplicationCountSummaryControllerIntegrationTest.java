package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.APPLICANT;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;

public class ApplicationCountSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationCountSummaryController> {


    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    @Override
    protected void setControllerUnderTest(ApplicationCountSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void applicationCountSummariesByCompetitionId() {
        Long competitionId = 1L;
        loginCompAdmin();
        ApplicationCountSummaryPageResource counts = controller.getApplicationCountSummariesByCompetitionId(competitionId,0,3, empty()).getSuccessObject();

        assertEquals(5, counts.getTotalElements());
        assertEquals(0, counts.getNumber());
        assertEquals(2, counts.getTotalPages());
        assertEquals(3, counts.getContent().size());

    }

    @Test
    public void applicationCountSummariesByCompetitionIdFiltered() {
        Long competitionId = 1L;
        loginCompAdmin();

        ApplicationCountSummaryPageResource counts = controller.getApplicationCountSummariesByCompetitionId(competitionId, 0, 6, ofNullable("3")).getSuccessObject();

        assertEquals(1, counts.getTotalElements());
        assertEquals(0, counts.getNumber());
        assertEquals(1, counts.getTotalPages());
        ApplicationCountSummaryResource summaryResource = counts.getContent().get(0);
        assertEquals(3L, (long) summaryResource.getId());
        assertEquals("Mobile Phone Data for Logistics Analytics", summaryResource.getName());
        assertEquals("Empire Ltd", summaryResource.getLeadOrganisation());
        assertEquals(3, summaryResource.getAssessors());
        assertEquals(2, summaryResource.getAccepted());
        assertEquals(2, summaryResource.getSubmitted());
    }

    @Test
    public void applicationCountSummariesByCompetitionIdAndInnovationArea() {
        Long competitionId = 1L;
        long innovationAreaId = 54L;
        long assessorId = 20L;
        loginCompAdmin();

        Application application = newApplication()
                .with(id(null))
                .withApplicationState(ApplicationState.SUBMITTED)
                .withName("Warp Drive")
                .withNoInnovationAreaApplicable(false)
                .withCompetition(competitionRepository.findById(competitionId))
                .withInnovationArea(innovationAreaRepository.findOne(innovationAreaId))
                .build();
        application.getApplicationProcess().setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED));

        applicationRepository.save(application);

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withRole(newRole().with(id(1L)).withType(LEADAPPLICANT).build())
                .withOrganisationId(3L)
                .withUser(userMapper.mapToDomain(getSteveSmith()))
                .build();

        processRoleRepository.save(processRole);

        flushAndClearSession();

        ApplicationCountSummaryPageResource counts = controller.getApplicationCountSummariesByCompetitionIdAndInnovationArea(competitionId, assessorId, 0, 6, "", ofNullable(innovationAreaId)).getSuccessObject();

        assertEquals(1, counts.getTotalElements());
        assertEquals(0, counts.getNumber());
        assertEquals(1, counts.getTotalPages());
        ApplicationCountSummaryResource summaryResource = counts.getContent().get(0);
        assertEquals((long) application.getId(), (long) summaryResource.getId());
        assertEquals("Warp Drive", summaryResource.getName());
        assertEquals("Empire Ltd", summaryResource.getLeadOrganisation());
        assertEquals(0, summaryResource.getAssessors());
        assertEquals(0, summaryResource.getAccepted());
        assertEquals(0, summaryResource.getSubmitted());
    }
}
