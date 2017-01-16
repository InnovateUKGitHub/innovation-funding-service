package org.innovateuk.ifs.management.viewmodel;

/**
 * TODO
 */
abstract class ApplicationAssessmentProgressRowViewModel {

    private String name;
    private int totalApplicationsCount;
    private int assignedCount;

    protected ApplicationAssessmentProgressRowViewModel(String name, int totalApplicationsCount, int assignedCount) {
        this.name = name;
        this.totalApplicationsCount = totalApplicationsCount;
        this.assignedCount = assignedCount;
    }

    public String getName() {
        return name;
    }

    public int getTotalApplicationsCount() {
        return totalApplicationsCount;
    }

    public int getAssignedCount() {
        return assignedCount;
    }
}
