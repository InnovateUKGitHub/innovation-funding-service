package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors view.
 */
public abstract class InviteAssessorsViewModel<ViewModelRowType extends InviteAssessorsRowViewModel> {

    private Long competitionId;
    private String competitionName;
    private int assessorsInvited;
    private int assessorsAccepted;
    private int assessorsDeclined;
    private int assessorsStaged;
    private String innovationSector;
    private String innovationArea;
    private List<ViewModelRowType> assessors;

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

    public int getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(int assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }

    public int getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(int assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

    public int getAssessorsDeclined() {
        return assessorsDeclined;
    }

    public void setAssessorsDeclined(int assessorsDeclined) {
        this.assessorsDeclined = assessorsDeclined;
    }

    public int getAssessorsStaged() {
        return assessorsStaged;
    }

    public void setAssessorsStaged(int assessorsStaged) {
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
}
