package com.worth.ifs.assessment.model;

import com.worth.ifs.application.UserApplicationRole;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardApplicationViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.worth.ifs.assessment.resource.AssessmentStates.SUBMITTED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.partitioningBy;

/**
 * Build the model for the Assessor Competition Dashboard view.
 */
@Component
public class AssessorCompetitionDashboardModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    public AssessorCompetitionDashboardViewModel populateModel(Long competitionId, Long userId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        LocalDateTime acceptDeadline = competition.getAssessorAcceptsDate();
        LocalDateTime submitDeadline = competition.getAssessorDeadlineDate();

        Map<Boolean, List<AssessorCompetitionDashboardApplicationViewModel>> applicationsPartitionedBySubmitted =
                getApplicationsPartitionedBySubmitted(userId, competitionId);
        List<AssessorCompetitionDashboardApplicationViewModel> submitted = applicationsPartitionedBySubmitted.get(TRUE);
        List<AssessorCompetitionDashboardApplicationViewModel> outstanding = applicationsPartitionedBySubmitted.get(FALSE);

        boolean submitVisible = outstanding.stream()
                .filter(AssessorCompetitionDashboardApplicationViewModel::isReadyToSubmit)
                .findAny()
                .isPresent();

        return new AssessorCompetitionDashboardViewModel(
                competition.getName(),
                competition.getDescription(),
                competition.getLeadTechnologistName(),
                acceptDeadline,
                submitDeadline,
                submitted,
                outstanding,
                submitVisible
        );
    }

    private Map<Boolean, List<AssessorCompetitionDashboardApplicationViewModel>> getApplicationsPartitionedBySubmitted(Long userId, Long competitionId) {
        return assessmentService.getByUserAndCompetition(userId, competitionId).stream()
                .collect(partitioningBy(this::isAssessmentSubmitted, mapping(this::createApplicationViewModel, Collectors.toList())));
    }

    private boolean isAssessmentSubmitted(AssessmentResource assessmentResource) {
        return SUBMITTED == assessmentResource.getAssessmentState();
    }

    private AssessorCompetitionDashboardApplicationViewModel createApplicationViewModel(AssessmentResource assessment) {
        ApplicationResource application = applicationService.getById(assessment.getApplication());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        return new AssessorCompetitionDashboardApplicationViewModel(application.getId(),
                assessment.getId(),
                application.getApplicationDisplayName(),
                leadOrganisation.get().getName(),
                assessment.getAssessmentState());
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
                .findFirst();
    }
}