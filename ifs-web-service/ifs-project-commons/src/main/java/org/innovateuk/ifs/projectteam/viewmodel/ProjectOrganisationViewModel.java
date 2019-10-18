package org.innovateuk.ifs.projectteam.viewmodel;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * View model for an organisation, used on the Project Team page for Project Setup
 */
public class ProjectOrganisationViewModel implements Comparable<ProjectOrganisationViewModel> {
    private final List<ProjectOrganisationUserRowViewModel> users;
    private final String name;
    private final long id;
    private final boolean lead;
    private final boolean editable;
    private final Long partnerInviteId;

    private boolean openAddTeamMemberForm;

    public ProjectOrganisationViewModel(List<ProjectOrganisationUserRowViewModel> users,
                                        String name,
                                        long id,
                                        boolean lead,
                                        boolean editable,
                                        Long partnerInviteId) {
        this.users = users;
        this.name = name;
        this.lead = lead;
        this.id = id;
        this.editable = editable;
        this.partnerInviteId = partnerInviteId;
        this.openAddTeamMemberForm = false;
    }

    public ProjectOrganisationUserRowViewModel getFinanceContact() {
        return simpleFindFirst(users,
                               ProjectOrganisationUserRowViewModel::isFinanceContact).orElse(null);
    }

    public ProjectOrganisationUserRowViewModel getProjectManager() {
        return simpleFindFirst(users,
                               ProjectOrganisationUserRowViewModel::isProjectManager).orElse(null);
    }

    public List<ProjectOrganisationUserRowViewModel> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isOpenAddTeamMemberForm() {
        return openAddTeamMemberForm;
    }

    public boolean isEditable() {
        return editable;
    }

    public Long getPartnerInviteId() {
        return partnerInviteId;
    }

    public void setOpenAddTeamMemberForm(boolean openAddTeamMemberForm) {
        this.openAddTeamMemberForm = openAddTeamMemberForm;
    }

    public boolean isPartnerInvite() {
        return partnerInviteId != null;
    }
    @Override
    public int compareTo(ProjectOrganisationViewModel that) {
        if(this.editable && this.lead) {
            return -1;
        } else if (that.editable && that.lead) {
            return 1;
        } else if (this.editable) {
            return -1;
        } else if (that.editable) {
            return 1;
        } else if (this.lead) {
            return -1;
        } else if (that.lead) {
            return 1;
        }
        return 0;
    }
}
