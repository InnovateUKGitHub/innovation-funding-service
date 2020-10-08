package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.pagination.PaginationViewModel;

import java.util.List;

public class ViewCofundersViewModel {

    private long competitionId;
    private String competitionName;
    private List<ApplicationsForCofundingResource> rows;
    private final PaginationViewModel pagination;

    public ViewCofundersViewModel(CompetitionResource competitionResource, List<ApplicationsForCofundingResource> rows, PaginationViewModel pagination) {
        this.competitionId = competitionResource.getId();
        this.competitionName = competitionResource.getName();
        this.rows = rows;
        this.pagination = pagination;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public List<ApplicationsForCofundingResource> getRows() {
        return rows;
    }

    public void setRows(List<ApplicationsForCofundingResource> rows) {
        this.rows = rows;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }
}
