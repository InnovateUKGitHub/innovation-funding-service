package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model for a project user in an organisation, used on the Project Team page for Project Setup
 */
public class ProjectOrganisationUserRowViewModel {

    private static final String PROJECT_MANAGER_ROLE_NAME = "Project manager";
    private static final String FINANCE_CONTACT_ROLE_NAME = "Finance contact";
    private static final String PARTNER_ROLE_NAME = "-";
    private final String email;
    private final String userName;
    private final long userId;
    private boolean isProjectManager;
    private boolean isFinanceContact;

    public ProjectOrganisationUserRowViewModel(String email,
                                               String userName,
                                               long userId,
                                               boolean isProjectManager,
                                               boolean isFinanceContact) {
        this.email = email;
        this.userName = userName;
        this.userId = userId;
        this.isProjectManager = isProjectManager;
        this.isFinanceContact = isFinanceContact;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isFinanceContact() {
        return isFinanceContact;
    }

    public void setFinanceContact(boolean isFinanceContact) {
        this.isFinanceContact = isFinanceContact;
    }

    public boolean isProjectManager() {
        return isProjectManager;
    }

    public void setProjectManager(boolean isProjectManager) {
        this.isProjectManager = isProjectManager;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectOrganisationUserRowViewModel that = (ProjectOrganisationUserRowViewModel) o;

        return new EqualsBuilder()
                .append(email, that.email)
                .append(userName, that.userName)
                .append(userId, that.userId)
                .append(isFinanceContact, that.isFinanceContact)
                .append(isProjectManager, that.isProjectManager)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(email)
                .append(userName)
                .append(userId)
                .append(isFinanceContact)
                .append(isProjectManager)
                .toHashCode();
    }


}
