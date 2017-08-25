package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManageAssessorsViewModel {
    private final long competitionId;
    private final String competitionName;
    private final List<ManageAssessorsRowViewModel> assessors;
    private final boolean inAssessment;
    private final List<InnovationSectorResource> innovationSectors;
    private final PaginationViewModel pagination;

    public ManageAssessorsViewModel(long competitionId,
                                    String competitionName,
                                    List<ManageAssessorsRowViewModel> assessors,
                                    boolean inAssessment,
                                    List<InnovationSectorResource> innovationSectors,
                                    PaginationViewModel pagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessors = assessors;
        this.inAssessment = inAssessment;
        this.innovationSectors = innovationSectors;
        this.pagination = pagination;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ManageAssessorsRowViewModel> getAssessors() {
        return assessors;
    }

    public boolean isInAssessment() {
        return inAssessment;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public List<InnovationSectorResource> getInnovationSectors() {
        return innovationSectors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ManageAssessorsViewModel that = (ManageAssessorsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(inAssessment, that.inAssessment)
                .append(competitionName, that.competitionName)
                .append(assessors, that.assessors)
                .append(assessors, that.assessors)
                .append(innovationSectors, that.innovationSectors)
                .append(pagination, that.pagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(assessors)
                .append(inAssessment)
                .append(innovationSectors)
                .append(pagination)
                .toHashCode();
    }
}