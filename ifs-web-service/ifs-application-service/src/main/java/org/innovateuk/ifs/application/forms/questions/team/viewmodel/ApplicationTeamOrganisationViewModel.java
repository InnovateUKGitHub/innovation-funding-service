package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;

import java.util.List;

public class ApplicationTeamOrganisationViewModel implements Comparable<ApplicationTeamOrganisationViewModel> {

    private final long id;
    private final Long inviteId;
    private final String name;
    private final String type;
    private final List<ApplicationTeamRowViewModel> rows;
    private final boolean editable;
    private final boolean userBelongsToOrganisation;
    private final boolean existing;
    private final AddressResource address;

    private boolean openAddTeamMemberForm;
    public ApplicationTeamOrganisationViewModel(long id, Long inviteId, String name, String type, List<ApplicationTeamRowViewModel> rows, boolean editable, boolean existing) {
        this(id, inviteId, name, type, rows, editable, false, existing, null);
    }

    public ApplicationTeamOrganisationViewModel(long id, Long inviteId, String name, String type, List<ApplicationTeamRowViewModel> rows, boolean editable, boolean existing, boolean userBelongsToOrganisation, AddressResource address) {
        this.id = id;
        this.inviteId = inviteId;
        this.name = name;
        this.type = type;
        this.rows = rows;
        this.editable = editable;
        this.userBelongsToOrganisation = userBelongsToOrganisation;
        this.existing = existing;
        this.address = address;
        this.openAddTeamMemberForm = false;
    }

    public long getId() {
        return id;
    }

    public Long getInviteId() {
        return inviteId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<ApplicationTeamRowViewModel> getRows() {
        return rows;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isUserBelongsToOrganisation() {
        return userBelongsToOrganisation;
    }

    public boolean isExisting() {
        return existing;
    }

    public AddressResource getAddress() {
        return address;
    }

    public boolean isOpenAddTeamMemberForm() {
        return openAddTeamMemberForm;
    }

    public void setOpenAddTeamMemberForm(boolean openAddTeamMemberForm) {
        this.openAddTeamMemberForm = openAddTeamMemberForm;
    }

    public boolean isLead() {
        return rows.stream().anyMatch(ApplicationTeamRowViewModel::isLead);
    }

    public boolean isSingleUserRemaining() {
        return rows.size() == 1;
    }

    @Override
    public int compareTo(ApplicationTeamOrganisationViewModel that) {
        if (this.isLead()) {
            return -1;
        } else if (that.isLead()) {
            return 1;
        }
        return name.compareTo(that.getName());
    }
}
