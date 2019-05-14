package org.innovateuk.ifs.projectteam.viewmodel;

import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.List;

/**
 * View model for an organisation, used on the Project Team page for Project Setup
 */
public class ProjectOrganisationViewModel implements Comparable<ProjectOrganisationViewModel> {

    private List<ProjectOrganisationUserRowViewModel> users;

    private String orgName;

    private long orgId;

    private boolean leadOrg;

    private boolean openAddTeamMemberForm;

    private boolean editable;

    public ProjectOrganisationViewModel(List<ProjectOrganisationUserRowViewModel> users,
                                        String orgName,
                                        long orgId,
                                        boolean leadOrg,
                                        boolean editable) {
        this.users = users;
        this.orgName = orgName;
        this.leadOrg = leadOrg;
        this.orgId = orgId;
        this.editable = editable;
        this.openAddTeamMemberForm = false;
    }

    public ProjectOrganisationUserRowViewModel getFinanceContact() {
        return CollectionFunctions.simpleFindFirst(users,
                                                   ProjectOrganisationUserRowViewModel::isFinanceContact).orElse(null);
    }

    public ProjectOrganisationUserRowViewModel getProjectManager() {
        return CollectionFunctions.simpleFindFirst(users,
                                                   ProjectOrganisationUserRowViewModel::isProjectManager).orElse(null);
    }

    public List<ProjectOrganisationUserRowViewModel> getUsers() {
        return users;
    }

    public void setUsers(List<ProjectOrganisationUserRowViewModel> users) {
        this.users = users;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public boolean isLeadOrg() {
        return leadOrg;
    }

    public void setLeadOrg(boolean leadOrg) {
        this.leadOrg = leadOrg;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public boolean isOpenAddTeamMemberForm() {
        return openAddTeamMemberForm;
    }

    public void setOpenAddTeamMemberForm(boolean openAddTeamMemberForm) {
        this.openAddTeamMemberForm = openAddTeamMemberForm;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public int compareTo(ProjectOrganisationViewModel that) {
        if(this.editable && this.leadOrg) {
            return -1;
        } else if (that.editable && that.leadOrg) {
            return 1;
        } else if (this.editable) {
            return -1;
        } else if (that.editable) {
            return 1;
        } else if (this.leadOrg) {
            return -1;
        } else if (that.leadOrg) {
            return 1;
        }
        return 0;
    }
}
