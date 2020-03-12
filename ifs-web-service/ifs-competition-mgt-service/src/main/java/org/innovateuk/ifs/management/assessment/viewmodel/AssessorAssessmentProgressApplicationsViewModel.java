package org.innovateuk.ifs.management.assessment.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.pagination.PaginationViewModel;

import java.util.List;

public class AssessorAssessmentProgressApplicationsViewModel {
    private List<AssessorAssessmentProgressApplicationRowViewModel> applications;
    private boolean inAssessment;
    private Sort currentSort;
    private PaginationViewModel pagination;
    private long totalApplications;

    public AssessorAssessmentProgressApplicationsViewModel(List<AssessorAssessmentProgressApplicationRowViewModel> applications,
                                                           boolean inAssessment,
                                                           Sort currentSort,
                                                           PaginationViewModel pagination,
                                                           long totalApplications) {
        this.applications = applications;
        this.inAssessment = inAssessment;
        this.currentSort = currentSort;
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

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public Sort getCurrentSort() {
        return currentSort;
    }
}
