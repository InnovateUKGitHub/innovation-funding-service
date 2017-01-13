package org.innovateuk.ifs.assessment.model;

import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.viewmodel.AssessmentAssignmentViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
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
        AssessmentResource assessment = getAssignableAssessment(assessmentId);
        ApplicationResource application = getApplication(assessment.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> processRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        String projectSummary = getProjectSummary(application);
        SortedSet<OrganisationResource> collaborators = getApplicationOrganisations(processRoles);
        OrganisationResource leadPartner = getApplicationLeadOrganisation(processRoles).orElse(null);
        return new AssessmentAssignmentViewModel(assessmentId, competition.getId(), application, collaborators, leadPartner, projectSummary);
    }

    private AssessmentResource getAssignableAssessment(final Long assessmentId) {
        return assessmentService.getAssignableById(assessmentId);
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
