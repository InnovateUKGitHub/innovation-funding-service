package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors view.
 */
public abstract class InviteAssessorsViewModel<ViewModelRowType extends InviteAssessorsRowViewModel> {

    private Long competitionId;
    private String competitionName;
    private long assessorsInvited;
    private long assessorsAccepted;
    private long assessorsDeclined;
    private String innovationSector;
    private String innovationArea;
    private List<ViewModelRowType> assessors;
    private Pagination pagination;
    private String originQuery;

    protected InviteAssessorsViewModel() {
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

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public void setOriginQuery(String originQuery) {
        this.originQuery = originQuery;
    }
}