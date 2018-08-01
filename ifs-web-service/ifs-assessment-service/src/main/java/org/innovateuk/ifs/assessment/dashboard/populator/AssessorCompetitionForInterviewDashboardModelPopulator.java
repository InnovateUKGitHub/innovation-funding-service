package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
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

    private CompetitionRestService competitionRestService;
    private ApplicationService applicationService;
    private UserRestService userRestService;
    private OrganisationService organisationService;
    private InterviewAllocationRestService interviewAllocateRestService;

    @Autowired
    public AssessorCompetitionForInterviewDashboardModelPopulator(CompetitionRestService competitionRestService,
                                                                  ApplicationService applicationService,
                                                                  UserRestService userRestService,
                                                                  InterviewAllocationRestService interviewAllocateRestService,
                                                                  OrganisationService organisationService) {
        this.competitionRestService = competitionRestService;
        this.applicationService = applicationService;
        this.userRestService = userRestService;
        this.interviewAllocateRestService = interviewAllocateRestService;
        this.organisationService = organisationService;
    }

    public AssessorCompetitionForInterviewDashboardViewModel populateModel(long competitionId, long userId, String originQuery) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications = getApplications(userId, competitionId);

        return new AssessorCompetitionForInterviewDashboardViewModel(
                competition.getId(),
                competition.getName(),
                competition.getLeadTechnologistName(),
                applications,
                originQuery);
    }

    private List<AssessorCompetitionForInterviewDashboardApplicationViewModel> getApplications(long userId, long competitionId) {
        List<InterviewResource> interviews = interviewAllocateRestService.getAllocatedApplicationsByAssessorId(competitionId, userId).getSuccess();
        return simpleMap(interviews, interview -> createApplicationViewModel(interview));
    }

    private AssessorCompetitionForInterviewDashboardApplicationViewModel createApplicationViewModel(InterviewResource assessmentInterview) {
        ApplicationResource application = applicationService.getById(assessmentInterview.getApplication());
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        Optional<OrganisationResource> leadOrganisation = organisationService.getApplicationLeadOrganisation(userApplicationRoles);

        return new AssessorCompetitionForInterviewDashboardApplicationViewModel(application.getId(),
                application.getName(),
                leadOrganisation.map(OrganisationResource::getName).orElse(EMPTY)
        );
    }
}
