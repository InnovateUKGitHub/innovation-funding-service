package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAvailableAssessorsRowViewModel that = (ApplicationAvailableAssessorsRowViewModel) o;

        return new EqualsBuilder()
                .append(totalApplications, that.totalApplications)
                .append(assignedApplications, that.assignedApplications)
                .append(submittedApplications, that.submittedApplications)
                .append(name, that.name)
                .append(skillAreas, that.skillAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(skillAreas)
                .append(totalApplications)
                .append(assignedApplications)
                .append(submittedApplications)
                .toHashCode();
    }
}
