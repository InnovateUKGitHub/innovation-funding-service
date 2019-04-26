package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectOrganisationViewModel {

    private List<ProjectUserResource> users;

    private String orgName;

    private long orgId;

    private boolean leadOrg;

    public ProjectOrganisationViewModel(List<ProjectUserResource> users,
                                        String orgName,
                                        long orgId,
                                        boolean leadOrg) {
        this.users = users;
        this.orgName = orgName;
        this.leadOrg = leadOrg;
        this.orgId = orgId;
    }

    public ProjectUserResource getFinanceContact() {
        return simpleFindFirst(users,
                               ProjectUserResource::isFinanceContact).orElse(null);
    }

    public ProjectUserResource getProjectManager() {
        return simpleFindFirst(users,
                               ProjectUserResource::isProjectManager).orElse(null);
    }

    public List<ProjectUserResource> getUsers() {
        return users;
    }

    public void setUsers(List<ProjectUserResource> users) {
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
