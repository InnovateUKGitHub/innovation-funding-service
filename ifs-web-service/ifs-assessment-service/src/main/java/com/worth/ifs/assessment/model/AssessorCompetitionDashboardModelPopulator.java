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
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

    public AssessorCompetitionDashboardViewModel populateModel(Long competitionId, Long userId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        String leadTechnologist = getLeadTechnologist(competition);
        LocalDateTime acceptDeadline = competition.getAssessorAcceptsDate();
        LocalDateTime submitDeadline = competition.getAssessorDeadlineDate();
        return new AssessorCompetitionDashboardViewModel(competition.getName(), competition.getDescription(), leadTechnologist, acceptDeadline, submitDeadline,
                getApplications(userId, competitionId));
    }

    private List<AssessorCompetitionDashboardApplicationViewModel> getApplications(Long userId, Long competitionId) {
        List<AssessmentResource> assessmentList = assessmentService.getByUserAndCompetition(userId, competitionId);

        return assessmentList.stream()
                .map(assessment -> {
                    ApplicationResource application = applicationService.getById(assessment.getApplication());
                    List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
                    Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
                    return new AssessorCompetitionDashboardApplicationViewModel(application.getId(),
                            assessment.getId(),
                            application.getApplicationDisplayName(),
                            leadOrganisation.get().getName(),
                            assessment.getAssessmentState());
                }).collect(Collectors.toList());
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
                .findFirst();
    }

    private String getLeadTechnologist(CompetitionResource competition) {
        return Optional.ofNullable(competition.getLeadTechnologist()).map(leadTechnologistId -> userService.findById(leadTechnologistId).getName()).orElse(null);
    }
}