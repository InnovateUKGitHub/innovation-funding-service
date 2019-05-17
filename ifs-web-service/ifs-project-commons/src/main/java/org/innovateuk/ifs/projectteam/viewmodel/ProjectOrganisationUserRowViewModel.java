package org.innovateuk.ifs.projectteam.viewmodel;

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
    private final String name;
    private final long id;
    private final boolean invite;

    private boolean isProjectManager;
    private boolean isFinanceContact;

    public ProjectOrganisationUserRowViewModel(String email,
                                               String name,
                                               long id,
                                               boolean isProjectManager,
                                               boolean isFinanceContact,
                                               boolean invite) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.isProjectManager = isProjectManager;
        this.isFinanceContact = isFinanceContact;
        this.invite = invite;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
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

    public boolean isInvite() {
        return invite;
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

    public String getDisplayName() {
        return invite ? name + " (Pending)" : name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectOrganisationUserRowViewModel that = (ProjectOrganisationUserRowViewModel) o;

        return new EqualsBuilder()
                .append(email, that.email)
                .append(name, that.name)
                .append(id, that.id)
                .append(isFinanceContact, that.isFinanceContact)
                .append(isProjectManager, that.isProjectManager)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(email)
                .append(name)
                .append(id)
                .append(isFinanceContact)
                .append(isProjectManager)
                .toHashCode();
    }


}
