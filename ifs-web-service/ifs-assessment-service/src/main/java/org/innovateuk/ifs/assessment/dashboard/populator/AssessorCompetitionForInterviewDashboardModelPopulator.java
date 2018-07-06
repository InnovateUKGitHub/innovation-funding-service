package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Assessor interview Dashboard view.
 */
@Component
public class AssessorCompetitionForInterviewDashboardModelPopulator {

    private CompetitionService competitionService;
    private ApplicationService applicationService;
    private ProcessRoleService processRoleService;
    private OrganisationService organisationService;
    private InterviewAllocationRestService interviewAllocateRestService;

    @Autowired
    public AssessorCompetitionForInterviewDashboardModelPopulator(CompetitionService competitionService,
                                                                  ApplicationService applicationService,
                                                                  ProcessRoleService processRoleService,
                                                                  InterviewAllocationRestService interviewAllocateRestService,
                                                                  OrganisationService organisationService) {
        this.competitionService = competitionService;
        this.applicationService = applicationService;
        this.processRoleService = processRoleService;
        this.interviewAllocateRestService = interviewAllocateRestService;
        this.organisationService = organisationService;
    }

    public AssessorCompetitionForInterviewDashboardViewModel populateModel(long competitionId, long userId) {
        CompetitionResource competition = competitionService.getById(competitionId);

        List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications = getApplications(userId, competitionId);

        return new AssessorCompetitionForInterviewDashboardViewModel(
                competition.getId(),
                competition.getName(),
                competition.getLeadTechnologistName(),
                applications
        );
    }

    private List<AssessorCompetitionForInterviewDashboardApplicationViewModel> getApplications(long userId, long competitionId) {
        List<InterviewResource> interviews = interviewAllocateRestService.getAllocatedApplicationsByAssessorId(competitionId, userId).getSuccess();
        return simpleMap(interviews, interview -> createApplicationViewModel(interview));
    }

    private AssessorCompetitionForInterviewDashboardApplicationViewModel createApplicationViewModel(InterviewResource assessmentInterview) {
        ApplicationResource application = applicationService.getById(assessmentInterview.getApplication());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> leadOrganisation = organisationService.getApplicationLeadOrganisation(userApplicationRoles);

        return new AssessorCompetitionForInterviewDashboardApplicationViewModel(application.getId(),
                application.getName(),
                leadOrganisation.map(OrganisationResource::getName).orElse(EMPTY)
        );
    }
}
