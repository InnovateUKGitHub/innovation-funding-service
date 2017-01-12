package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationAssessmentSummaryRestService;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Application Progress view.
 */
@Component
public class ApplicationAssessmentProgressModelPopulator {

    @Autowired
    private ApplicationAssessmentSummaryRestService applicationAssessmentSummaryRestService;

    public ApplicationAssessmentProgressViewModel populateModel(Long applicationId) {
        ApplicationAssessmentSummaryResource applicationAssessmentSummary = applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId).getSuccessObjectOrThrowException();

        return new ApplicationAssessmentProgressViewModel(applicationAssessmentSummary.getId(),
                applicationAssessmentSummary.getName(),
                applicationAssessmentSummary.getCompetitionId(),
                applicationAssessmentSummary.getCompetitionName(),
                applicationAssessmentSummary.getPartnerOrganisations());
    }
}