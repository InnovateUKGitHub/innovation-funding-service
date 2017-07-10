package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

public class AssessorAssessmentProgressViewModel {

    private final String assessorName;
    private final List<String> innovationAreas;
    private final BusinessType businessType;
    private final int totalApplications;

    private final List<AssessorAssessmentProgressAssignedRowViewModel> assigned;

    public AssessorAssessmentProgressViewModel(String assessorName,
                                               List<String> innovationAreas,
                                               BusinessType businessType,
                                               int totalApplications,
                                               List<AssessorAssessmentProgressAssignedRowViewModel> assigned) {
        this.assessorName = assessorName;
        this.innovationAreas = innovationAreas;
        this.businessType = businessType;
        this.totalApplications = totalApplications;
        this.assigned = assigned;
    }

    public String getAssessorName() {
        return assessorName;
    }

    public List<String> getInnovationAreas() {
        return innovationAreas;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public List<AssessorAssessmentProgressAssignedRowViewModel> getAssigned() {
        return assigned;
    }
}
