package org.innovateuk.ifs.management.assessment.viewmodel;

import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;
import java.util.Optional;

public class AssessorAssessmentProgressApplicationsViewModel {
    private List<AssessorAssessmentProgressApplicationRowViewModel> applications;
    private boolean inAssessment;
    private Optional<Long> innovationArea;
    private String sortField;
    private Pagination pagination;
    private long totalApplications;

    public AssessorAssessmentProgressApplicationsViewModel(List<AssessorAssessmentProgressApplicationRowViewModel> applications,
                                                           boolean inAssessment,
                                                           Optional<Long> innovationArea,
                                                           String sortField,
                                                           Pagination pagination,
                                                           long totalApplications) {
        this.applications = applications;
        this.inAssessment = inAssessment;
        this.innovationArea = innovationArea;
        this.sortField = sortField;
        this.pagination = pagination;
        this.totalApplications = totalApplications;
    }

    public List<AssessorAssessmentProgressApplicationRowViewModel> getApplications() {
        return applications;
    }

    public boolean getInAssessment() {
        return inAssessment;
    }

    public boolean isInAssessment() {
        return inAssessment;
    }

    public Optional<Long> getInnovationArea() {
        return innovationArea;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public String getSortField() {
        return sortField;
    }
}
