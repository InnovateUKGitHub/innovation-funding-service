package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for an assessor in the Application Progress view.
 */
abstract class ApplicationAssessmentProgressRowViewModel {

    private String name;
    private long totalApplicationsCount;
    private long assignedCount;

    protected ApplicationAssessmentProgressRowViewModel(String name, long totalApplicationsCount, long assignedCount) {
        this.name = name;
        this.totalApplicationsCount = totalApplicationsCount;
        this.assignedCount = assignedCount;
    }

    public String getName() {
        return name;
    }

    public long getTotalApplicationsCount() {
        return totalApplicationsCount;
    }

    public long getAssignedCount() {
        return assignedCount;
    }
}
