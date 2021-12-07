package org.innovateuk.ifs.assessment.assignment.populator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.assessment.assignment.viewmodel.AssessmentAssignmentViewModel;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;

/**
 * Build the model for the Assessment Assignment view.
 */
@Component
public class AssessmentAssignmentModelPopulator {

    private AssessmentService assessmentService;
    private CompetitionRestService competitionRestService;
    private ApplicationRestService applicationRestService;
    private ProcessRoleRestService processRoleRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private OrganisationService organisationService;

    public AssessmentAssignmentModelPopulator(AssessmentService assessmentService,
                                              CompetitionRestService competitionRestService,
                                              ApplicationRestService applicationRestService,
                                              ProcessRoleRestService processRoleRestService,
                                              FormInputResponseRestService formInputResponseRestService,
                                              OrganisationService organisationService) {
        this.assessmentService = assessmentService;
        this.competitionRestService = competitionRestService;
        this.applicationRestService = applicationRestService;
        this.processRoleRestService = processRoleRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.organisationService = organisationService;
    }

    public AssessmentAssignmentViewModel populateModel(Long assessmentId) {
        AssessmentResource assessment = assessmentService.getAssignableById(assessmentId);
        CompetitionResource competition = competitionRestService.getCompetitionById(assessment.getCompetition()).getSuccess();
        ApplicationResource application = applicationRestService.getApplicationById(assessment.getApplication()).getSuccess();
        String projectSummary = getProjectSummary(assessment);
        List<ProcessRoleResource> processRoles = processRoleRestService.findProcessRole(assessment.getApplication()).getSuccess();
        SortedSet<OrganisationResource> collaborators = organisationService.getApplicationOrganisations(processRoles);
        OrganisationResource leadPartner = organisationService.getApplicationLeadOrganisation(processRoles).orElse(null);
        return new AssessmentAssignmentViewModel(assessmentId, assessment.getCompetition(), assessment.getApplicationName(),
                competition.isAlwaysOpen(), application.getAssessmentPeriodId(), collaborators, leadPartner, projectSummary);
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
