package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors view.
 */
public abstract class PanelInviteAssessorsViewModel<ViewModelRowType extends PanelInviteAssessorsRowViewModel> {

    private Long competitionId;
    private String competitionName;
    private long assessorsInvited;
    private long assessorsAccepted;
    private long assessorsDeclined;
    private long assessorsStaged;
    private String innovationSector;
    private String innovationArea;
    private List<ViewModelRowType> assessors;
    private PaginationViewModel pagination;

    protected PanelInviteAssessorsViewModel() {
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public long getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(long assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }

    public long getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(long assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

    public long getAssessorsDeclined() {
        return assessorsDeclined;
    }

    public void setAssessorsDeclined(long assessorsDeclined) {
        this.assessorsDeclined = assessorsDeclined;
    }

    public long getAssessorsStaged() {
        return assessorsStaged;
    }

    public void setAssessorsStaged(long assessorsStaged) {
        this.assessorsStaged = assessorsStaged;
    }

    public String getInnovationSector() {
        return innovationSector;
    }

    public void setInnovationSector(String innovationSector) {
        this.innovationSector = innovationSector;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(String innovationArea) {
        this.innovationArea = innovationArea;
    }

    public List<ViewModelRowType> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<ViewModelRowType> assessors) {
        this.assessors = assessors;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public void setPagination(PaginationViewModel pagination) {
        this.pagination = pagination;
    }
}
