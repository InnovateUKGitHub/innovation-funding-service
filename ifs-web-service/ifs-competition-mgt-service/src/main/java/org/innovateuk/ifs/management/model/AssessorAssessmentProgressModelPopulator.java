package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.service.AssessorCompetitionSummaryRestService;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.management.viewmodel.AssessorAssessmentProgressAssignedRowViewModel;
import org.innovateuk.ifs.management.viewmodel.AssessorAssessmentProgressViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class AssessorAssessmentProgressModelPopulator {

    @Autowired
    private AssessorCompetitionSummaryRestService assessorCompetitionSummaryRestService;

    public AssessorAssessmentProgressViewModel populateModel(long competitionId, long assessorId) {
        AssessorCompetitionSummaryResource summaryResource = assessorCompetitionSummaryRestService
                .getAssessorSummary(assessorId, competitionId)
                .getSuccessObjectOrThrowException();

        List<String> innovationAreas = simpleMap(
                summaryResource.getAssessor().getProfile().getInnovationAreas(),
                CategoryResource::getName
        );

        List<AssessorAssessmentProgressAssignedRowViewModel> assigned = simpleMap(
                summaryResource.getAssignedAssessments(),
                assignedAssessment -> new AssessorAssessmentProgressAssignedRowViewModel(
                        assignedAssessment.getApplicationId(),
                        assignedAssessment.getApplicationName(),
                        assignedAssessment.getLeadOrganisation(),
                        assignedAssessment.getTotalAssessors()
                )
        );

        BusinessType businessType = summaryResource.getAssessor().getProfile().getBusinessType();
        return new AssessorAssessmentProgressViewModel(
                summaryResource.getCompetitionId(),
                summaryResource.getCompetitionName(),
                summaryResource.getAssessor().getUser().getName(),
                innovationAreas,
                businessType != null ? businessType.getDisplayName() : "",
                summaryResource.getTotalApplications(),
                assigned
        );
    }
}
