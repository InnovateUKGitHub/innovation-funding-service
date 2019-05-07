package org.innovateuk.ifs.project.projectteam.viewmodel;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * View model for an organisation, used on the Project Team page for Project Setup
 */
public class ProjectOrganisationViewModel {

    private List<ProjectOrganisationUserRowViewModel> users;

    private String orgName;

    private long orgId;

    private boolean leadOrg;

    public ProjectOrganisationViewModel(List<ProjectOrganisationUserRowViewModel> users,
                                        String orgName,
                                        long orgId,
                                        boolean leadOrg) {
        this.users = users;
        this.orgName = orgName;
        this.leadOrg = leadOrg;
        this.orgId = orgId;
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
}
