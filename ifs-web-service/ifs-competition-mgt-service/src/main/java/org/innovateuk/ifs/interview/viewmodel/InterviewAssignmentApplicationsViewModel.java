package org.innovateuk.ifs.interview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.management.core.viewmodel.PaginationViewModel;

import java.util.List;

/**
 * Base class for Assessment Interview Panel views.
 */
public abstract class InterviewAssignmentApplicationsViewModel<T> {

    private final long competitionId;
    private final String competitionName;
    private final List<T> applications;
    private final String innovationSector;
    private final String innovationArea;
    private final PaginationViewModel pagination;
    private final String originQuery;
    private final InterviewAssignmentKeyStatisticsResource keyStatisticsResource;

    protected InterviewAssignmentApplicationsViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<T> applications,
            InterviewAssignmentKeyStatisticsResource keyStatisticsResource,
            PaginationViewModel pagination,
            String originQuery) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.innovationArea = innovationArea;
        this.innovationSector = innovationSector;
        this.applications = applications;
        this.pagination = pagination;
        this.originQuery = originQuery;
        this.keyStatisticsResource = keyStatisticsResource;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public List<T> getApplications() {
        return applications;
    }

    public String getInnovationSector() {
        return innovationSector;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public int getApplicationsInCompetition() {
        return keyStatisticsResource.getApplicationsInCompetition();
    }

    public int getApplicationsInPanel() {
        return keyStatisticsResource.getApplicationsAssigned();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentApplicationsViewModel<?> that = (InterviewAssignmentApplicationsViewModel<?>) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(applications, that.applications)
                .append(innovationSector, that.innovationSector)
                .append(innovationArea, that.innovationArea)
                .append(pagination, that.pagination)
                .append(originQuery, that.originQuery)
                .append(keyStatisticsResource, that.keyStatisticsResource)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(applications)
                .append(innovationSector)
                .append(innovationArea)
                .append(pagination)
                .append(originQuery)
                .append(keyStatisticsResource)
                .toHashCode();
    }
}