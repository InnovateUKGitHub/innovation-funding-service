package org.innovateuk.ifs.management.assessor.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManageAssessorsViewModel {
    private final long competitionId;
    private final String competitionName;
    private final long assessmentPeriodId;
    private final String assessmentPeriodName;
    private final List<ManageAssessorsRowViewModel> assessors;
    private final boolean inAssessment;
    private final boolean alwaysOpen;
    private final List<InnovationSectorResource> innovationSectors;
    private final Pagination pagination;

    public ManageAssessorsViewModel(long competitionId,
                                    String competitionName,
                                    long assessmentPeriodId,
                                    String assessmentPeriodName,
                                    List<ManageAssessorsRowViewModel> assessors,
                                    boolean inAssessment,
                                    boolean alwaysOpen,
                                    List<InnovationSectorResource> innovationSectors,
                                    Pagination pagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessmentPeriodId = assessmentPeriodId;
        this.assessmentPeriodName = assessmentPeriodName;
        this.assessors = assessors;
        this.inAssessment = inAssessment;
        this.alwaysOpen = alwaysOpen;
        this.innovationSectors = innovationSectors;
        this.pagination = pagination;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public String getAssessmentPeriodName() {
        return assessmentPeriodName;
    }

    public boolean isOnlyAssessmentPeriod() {
        return assessmentPeriodName == null;
    }

    public List<ManageAssessorsRowViewModel> getAssessors() {
        return assessors;
    }

    public boolean isInAssessment() {
        return inAssessment;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }

    public Pagination getPagination() {
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
                .append(alwaysOpen, that.alwaysOpen)
                .append(competitionName, that.competitionName)
                .append(assessmentPeriodId, that.assessmentPeriodId)
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
                .append(assessmentPeriodId)
                .append(assessors)
                .append(inAssessment)
                .append(alwaysOpen)
                .append(innovationSectors)
                .append(pagination)
                .toHashCode();
    }
}