package org.innovateuk.ifs.projectteam.viewmodel;

/**
 * View model for a project user in an organisation, used on the Project Team page for Project Setup
 */
public class ProjectTeamUserViewModel extends AbstractProjectTeamRowViewModel {

    private static final String PROJECT_MANAGER_ROLE_NAME = "Project manager";
    private static final String FINANCE_CONTACT_ROLE_NAME = "Finance contact";
    private static final String PARTNER_ROLE_NAME = "-";

    private boolean financeContact;
    private boolean projectManager;

    public ProjectTeamUserViewModel(long id,
                                    String email,
                                    String name,
                                    boolean removeable) {
        super(id, email, name, removeable);
    }

    @Override
    public boolean isRemoveable() {
        return super.isRemoveable() && !isHasRole();
    }

    public boolean isFinanceContact() {
        return financeContact;
    }

    public void setFinanceContact(boolean financeContact) {
        this.financeContact = financeContact;
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    public void setProjectManager(boolean projectManager) {
        this.projectManager = projectManager;
    }

    public boolean isHasRole() {
        return isFinanceContact() || isProjectManager();
    }

    public String getRolesNames() {
        if(isProjectManager()) {
            return isFinanceContact() ?
                    PROJECT_MANAGER_ROLE_NAME + ", " + FINANCE_CONTACT_ROLE_NAME :
                    PROJECT_MANAGER_ROLE_NAME;
        }
        return isFinanceContact() ?
                FINANCE_CONTACT_ROLE_NAME : PARTNER_ROLE_NAME;
    }

    @Override
    public boolean isInvite() {
        return false;
    }
}
