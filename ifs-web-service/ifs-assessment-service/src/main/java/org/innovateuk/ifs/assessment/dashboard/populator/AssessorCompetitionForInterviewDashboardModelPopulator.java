package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.viewmodel.UserApplicationRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Assessor interview Dashboard view.
 */
@Component
public class AssessorCompetitionForInterviewDashboardModelPopulator {

    private CompetitionService competitionService;
    private ApplicationService applicationService;
    private OrganisationRestService organisationRestService;
    private ProcessRoleService processRoleService;
    private InterviewAllocationRestService interviewAllocateRestService;

    @Autowired
    public AssessorCompetitionForInterviewDashboardModelPopulator(CompetitionService competitionService,
                                                                  ApplicationService applicationService,
                                                                  OrganisationRestService organisationRestService,
                                                                  ProcessRoleService processRoleService,
                                                                  InterviewAllocationRestService interviewAllocateRestService) {
        this.competitionService = competitionService;
        this.applicationService = applicationService;
        this.organisationRestService = organisationRestService;
        this.processRoleService = processRoleService;
        this.interviewAllocateRestService = interviewAllocateRestService;
    }

    public AssessorCompetitionForInterviewDashboardViewModel populateModel(long competitionId, long userId, String origin, MultiValueMap<String, String> queryParams) {
        CompetitionResource competition = competitionService.getById(competitionId);

        List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications = getApplications(userId, competitionId);

        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.valueOf(origin), queryParams);

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
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);

        return new AssessorCompetitionForInterviewDashboardApplicationViewModel(application.getId(),
                application.getName(),
                leadOrganisation.map(OrganisationResource::getName).orElse(EMPTY)
        );
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .findFirst();
    }
}
