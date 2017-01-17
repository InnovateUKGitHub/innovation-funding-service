package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the available assessors shown in the 'Application progress' page
 */
public class ApplicationAvailableAssessorsRowViewModel extends ApplicationAssessmentProgressRowViewModel {

    private String skillAreas;
    private long submittedApplications;

    public ApplicationAvailableAssessorsRowViewModel(String name, String skillAreas, long totalApplications, long assignedApplications, long submittedApplications) {
        super(name, totalApplications, assignedApplications);
        this.skillAreas = skillAreas;
        this.submittedApplications = submittedApplications;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public void setSkillAreas(String skillAreas) {
        this.skillAreas = skillAreas;
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
                .appendSuper(super.equals(o))
                .append(submittedApplications, that.submittedApplications)
                .append(skillAreas, that.skillAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(skillAreas)
                .append(submittedApplications)
                .toHashCode();
    }
}
