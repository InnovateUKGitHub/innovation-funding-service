package org.innovateuk.ifs.projectteam.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;

import java.util.List;

/**
 * View model for an organisation, used on the Project Team page for Project Setup
 */
public class ProjectTeamOrganisationViewModel implements Comparable<ProjectTeamOrganisationViewModel> {
    private final List<AbstractProjectTeamRowViewModel> users;
    private final String name;
    private final long id;
    private final boolean lead;
    private final boolean canAddUsers;
    private final Long partnerInviteId;
    private final AddressResource address;

    private boolean openAddTeamMemberForm;

    public ProjectTeamOrganisationViewModel(List<AbstractProjectTeamRowViewModel> users,
                                            String name,
                                            long id,
                                            boolean lead,
                                            boolean canAddUsers,
                                            Long partnerInviteId,
                                            AddressResource address) {
        this.users = users;
        this.name = name;
        this.lead = lead;
        this.id = id;
        this.canAddUsers = canAddUsers;
        this.partnerInviteId = partnerInviteId;
        this.address = address;
        this.openAddTeamMemberForm = false;
    }

    public ProjectTeamUserViewModel getFinanceContact() {
        return users.stream()
                .filter(ProjectTeamUserViewModel.class::isInstance)
                .map(ProjectTeamUserViewModel.class::cast)
                .filter(ProjectTeamUserViewModel::isFinanceContact)
                .findFirst()
                .orElse(null);
    }

    public ProjectTeamUserViewModel getProjectManager() {
        return users.stream()
                .filter(ProjectTeamUserViewModel.class::isInstance)
                .map(ProjectTeamUserViewModel.class::cast)
                .filter(ProjectTeamUserViewModel::isProjectManager)
                .findFirst()
                .orElse(null);
    }

    public List<AbstractProjectTeamRowViewModel> getUsers() {
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

    public boolean isCanAddUsers() {
        return canAddUsers;
    }

    public Long getPartnerInviteId() {
        return partnerInviteId;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setOpenAddTeamMemberForm(boolean openAddTeamMemberForm) {
        this.openAddTeamMemberForm = openAddTeamMemberForm;
    }

    public boolean isPartnerInvite() {
        return partnerInviteId != null;
    }

    @Override
    public int compareTo(ProjectTeamOrganisationViewModel that) {
        if(this.canAddUsers && this.lead) {
            return -1;
        } else if (that.canAddUsers && that.lead) {
            return 1;
        } else if (this.canAddUsers) {
            return -1;
        } else if (that.canAddUsers) {
            return 1;
        } else if (this.lead) {
            return -1;
        } else if (that.lead) {
            return 1;
        }
        return 0;
    }
}
