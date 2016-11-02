package com.worth.ifs.assessment.model;

import com.worth.ifs.application.UserApplicationRole;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentAssignmentViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Build the model for the Assessment Assignment view.
 */
@Component
public class AssessmentAssignmentModelPopulator {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public AssessmentAssignmentViewModel populateModel(Long assessmentId) {
        AssessmentResource assessment = getAssessment(assessmentId);
        ApplicationResource application = getApplication(assessment.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> processRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        String projectSummary = getProjectSummary(application);
        SortedSet<OrganisationResource> collaborators = getApplicationOrganisations(processRoles);
        OrganisationResource leadPartner = getApplicationLeadOrganisation(processRoles).orElse(null);
        return new AssessmentAssignmentViewModel(assessmentId, competition.getId(), application, collaborators, leadPartner, projectSummary);
    }

    private AssessmentResource getAssessment(final Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(final Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
                .findFirst();
    }

    private SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRoleResource> userApplicationRoles) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                        || uar.getRoleName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
                .collect(Collectors.toCollection(supplier));
    }

    private String getProjectSummary(ApplicationResource application) {
        Long projectSummaryFormInputId = 11L;

        List<FormInputResponseResource> projectSummaryResponse = formInputResponseService.getByFormInputIdAndApplication(projectSummaryFormInputId, application.getId()).getSuccessObject();
        return projectSummaryResponse.size() > 0 ? projectSummaryResponse.get(0).getValue() : "";
    }
}
