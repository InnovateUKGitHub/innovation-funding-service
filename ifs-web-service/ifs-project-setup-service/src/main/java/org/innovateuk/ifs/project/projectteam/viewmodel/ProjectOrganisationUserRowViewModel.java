package org.innovateuk.ifs.project.projectteam.viewmodel;

public class ProjectOrganisationUserRowViewModel {

    private static final String PROJECT_MANAGER_ROLE_NAME = "Project manager";
    private static final String FINANCE_CONTACT_ROLE_NAME = "Finance contact";
    private static final String PARTNER_ROLE_NAME = "Partner";
    private final String email;
    private final String name;
    private final long userId;
    private final boolean isProjectManager;
    private final boolean isFinanceContact;

    public ProjectOrganisationUserRowViewModel(String email,
                                               String name,
                                               long userId,
                                               boolean isProjectManager,
                                               boolean isFinanceContact) {
        this.email = email;
        this.name = name;
        this.userId = userId;
        this.isProjectManager = isProjectManager;
        this.isFinanceContact = isFinanceContact;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isFinanceContact() {
        return isFinanceContact;
    }

    public boolean isProjectManager() {
        return isProjectManager;
    }

    public String getRolesNames() {
        if(isProjectManager) {
            return isFinanceContact ?
                    PROJECT_MANAGER_ROLE_NAME + ", " + FINANCE_CONTACT_ROLE_NAME :
                    PROJECT_MANAGER_ROLE_NAME;
        }
        return isFinanceContact?
                FINANCE_CONTACT_ROLE_NAME : PARTNER_ROLE_NAME;
    }
}
