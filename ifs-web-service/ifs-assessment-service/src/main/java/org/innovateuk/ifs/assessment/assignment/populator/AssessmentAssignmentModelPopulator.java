package org.innovateuk.ifs.assessment.assignment.populator;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.assessment.assignment.viewmodel.AssessmentAssignmentViewModel;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.PROJECT_SUMMARY;

/**
 * Build the model for the Assessment Assignment view.
 */
@Component
public class AssessmentAssignmentModelPopulator {

    private AssessmentService assessmentService;
    private ProcessRoleService processRoleService;
    private FormInputResponseRestService formInputResponseRestService;
    private OrganisationService organisationService;

    public AssessmentAssignmentModelPopulator(AssessmentService assessmentService,
                                              ProcessRoleService processRoleService,
                                              FormInputResponseRestService formInputResponseRestService,
                                              OrganisationService organisationService) {
        this.assessmentService = assessmentService;
        this.processRoleService = processRoleService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.organisationService = organisationService;
    }

    public AssessmentAssignmentViewModel populateModel(Long assessmentId) {
        AssessmentResource assessment = assessmentService.getAssignableById(assessmentId);
        String projectSummary = getProjectSummary(assessment);
        List<ProcessRoleResource> processRoles = processRoleService.findProcessRolesByApplicationId(assessment.getApplication());
        SortedSet<OrganisationResource> collaborators = organisationService.getApplicationOrganisations(processRoles);
        OrganisationResource leadPartner = organisationService.getApplicationLeadOrganisation(processRoles).orElse(null);
        return new AssessmentAssignmentViewModel(assessmentId, assessment.getCompetition(), assessment.getApplicationName(),
                collaborators, leadPartner, projectSummary);
    }

    private String getProjectSummary(AssessmentResource assessmentResource) {
        Optional<FormInputResponseResource> formInputResponseResource = formInputResponseRestService.getByApplicationIdAndQuestionSetupType(
                assessmentResource.getApplication(), PROJECT_SUMMARY).getOptionalSuccessObject();

        if(formInputResponseResource.isPresent()) {
            return formInputResponseResource.get().getValue();
        }
        return null;
    }
}
