package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;

import java.util.List;
import java.util.Objects;

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
    private final int applicationsInCompetition;
    private final int applicationsInPanel;

    protected InterviewAssignmentApplicationsViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<T> applications,
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.innovationArea = innovationArea;
        this.innovationSector = innovationSector;
        this.applications = applications;
        this.pagination = pagination;
        this.originQuery = originQuery;
        this.applicationsInCompetition = applicationsInCompetition;
        this.applicationsInPanel = applicationsInPanel;
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
        return applicationsInCompetition;
    }

    public int getApplicationsInPanel() {
        return applicationsInPanel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterviewAssignmentApplicationsViewModel<?> that = (InterviewAssignmentApplicationsViewModel<?>) o;
        return competitionId == that.competitionId &&
                applicationsInCompetition == that.applicationsInCompetition &&
                applicationsInPanel == that.applicationsInPanel &&
                Objects.equals(competitionName, that.competitionName) &&
                Objects.equals(applications, that.applications) &&
                Objects.equals(innovationSector, that.innovationSector) &&
                Objects.equals(innovationArea, that.innovationArea) &&
                Objects.equals(pagination, that.pagination) &&
                Objects.equals(originQuery, that.originQuery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competitionId, competitionName, applications, innovationSector, innovationArea, pagination, originQuery, applicationsInCompetition, applicationsInPanel);
    }

}