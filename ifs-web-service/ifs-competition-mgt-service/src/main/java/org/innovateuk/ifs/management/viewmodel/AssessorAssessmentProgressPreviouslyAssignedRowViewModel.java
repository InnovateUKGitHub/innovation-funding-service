package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * Holder of model attributes for a previously assigned applications in the Assessor Progress view.
 */
public class AssessorAssessmentProgressPreviouslyAssignedRowViewModel  {

    private long id;
    private String name;
    private String leadOrganisation;
    private long totalAssessors;

    public AssessorAssessmentProgressPreviouslyAssignedRowViewModel(long id,
                                                                    String name,
                                                                    String leadOrganisation,
                                                                    long totalAssessors) {
        this.id = id;
        this.name = name;
        this.leadOrganisation = leadOrganisation;
        this.totalAssessors = totalAssessors;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public long getTotalAssessors() {
        return totalAssessors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorAssessmentProgressPreviouslyAssignedRowViewModel that = (AssessorAssessmentProgressPreviouslyAssignedRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(totalAssessors, that.totalAssessors)
                .append(name, that.name)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(leadOrganisation)
                .append(totalAssessors)
                .toHashCode();
    }
}