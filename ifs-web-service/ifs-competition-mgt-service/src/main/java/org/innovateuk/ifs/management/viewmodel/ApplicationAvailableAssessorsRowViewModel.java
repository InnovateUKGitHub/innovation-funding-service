package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the available assessors shown in the 'Application progress' page
 */
public class ApplicationAvailableAssessorsRowViewModel {

    private String name;
    private String skillAreas;
    private long totalApplications;
    private long assignedApplications;
    private long submittedApplications;

    public ApplicationAvailableAssessorsRowViewModel(String name, String skillAreas, long totalApplications, long assignedApplications, long submittedApplications) {
        this.name = name;
        this.skillAreas = skillAreas;
        this.totalApplications = totalApplications;
        this.assignedApplications = assignedApplications;
        this.submittedApplications = submittedApplications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public void setSkillAreas(String skillAreas) {
        this.skillAreas = skillAreas;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getAssignedApplications() {
        return assignedApplications;
    }

    public void setAssignedApplications(long assignedApplications) {
        this.assignedApplications = assignedApplications;
    }

    public long getSubmittedApplications() {
        return submittedApplications;
    }

    public void setSubmittedApplications(long submittedApplications) {
        this.submittedApplications = submittedApplications;
    }
}
