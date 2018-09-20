package org.innovateuk.ifs.assessment.assignment.populator;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.assessment.assignment.viewmodel.AssessmentAssignmentViewModel;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;

/**
 * Build the model for the Assessment Assignment view.
 */
@Component
public class AssessmentAssignmentModelPopulator {

    private AssessmentService assessmentService;
    private UserRestService userRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private OrganisationService organisationService;

    public AssessmentAssignmentModelPopulator(AssessmentService assessmentService,
                                              UserRestService userRestService,
                                              FormInputResponseRestService formInputResponseRestService,
                                              OrganisationService organisationService) {
        this.assessmentService = assessmentService;
        this.userRestService = userRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.organisationService = organisationService;
    }

    public AssessmentAssignmentViewModel populateModel(Long assessmentId) {
        AssessmentResource assessment = assessmentService.getAssignableById(assessmentId);
        String projectSummary = getProjectSummary(assessment);
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(assessment.getApplication()).getSuccess();
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
