package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

/**
 * View model for Organisation creation - choosing organisation type
 */
public class OrganisationCreationViewModel {
    private List<OrganisationTypeResource> types;
    private String inviteOrganisationNameConfirmed;
    private String inviteOrganisationName;

    public OrganisationCreationViewModel(List<OrganisationTypeResource> types, ApplicationInviteResource invite) {
        this.types = types;
        this.inviteOrganisationName = invite.getInviteOrganisationName();
        this.inviteOrganisationNameConfirmed = getInviteOrganisationNameConfirmed();
    }

    public List<OrganisationTypeResource> getTypes() {
        return types;
    }

    public void setTypes(List<OrganisationTypeResource> types) {
        this.types = types;
    }

    public String getInviteOrganisationNameConfirmed() {
        return inviteOrganisationNameConfirmed;
    }

    public void setInviteOrganisationNameConfirmed(String inviteOrganisationNameConfirmed) {
        this.inviteOrganisationNameConfirmed = inviteOrganisationNameConfirmed;
    }

    public String getInviteOrganisationName() {
        return inviteOrganisationName;
    }

    public void setInviteOrganisationName(String inviteOrganisationName) {
        this.inviteOrganisationName = inviteOrganisationName;
    }
}
