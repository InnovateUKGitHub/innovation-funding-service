package org.innovateuk.ifs.management.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the available assessors shown in the 'Application progress' page
 */
public class ApplicationAvailableAssessorsRowViewModel extends ApplicationAssessmentProgressRowViewModel {

    private String skillAreas;
    private long submittedCount;

    public ApplicationAvailableAssessorsRowViewModel(long id,
                                                     String name,
                                                     String skillAreas,
                                                     long totalApplications,
                                                     long assignedApplications,
                                                     long submittedApplications) {
        super(id, name, totalApplications, assignedApplications);
        this.skillAreas = skillAreas;
        this.submittedCount = submittedApplications;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public void setSkillAreas(String skillAreas) {
        this.skillAreas = skillAreas;
    }

    public long getSubmittedApplications() {
        return submittedCount;
    }

    public void setSubmittedApplications(long submittedApplications) {
        this.submittedCount = submittedApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAvailableAssessorsRowViewModel that = (ApplicationAvailableAssessorsRowViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(submittedCount, that.submittedCount)
                .append(skillAreas, that.skillAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(skillAreas)
                .append(submittedCount)
                .toHashCode();
    }
}
