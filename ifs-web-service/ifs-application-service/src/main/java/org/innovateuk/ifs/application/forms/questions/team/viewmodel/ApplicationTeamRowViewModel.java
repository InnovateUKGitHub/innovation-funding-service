package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

public class ApplicationTeamRowViewModel {

    private final Long id;
    private final String name;
    private final String email;
    private final boolean lead;
    private final boolean invite;
    private final Long inviteId;

    public ApplicationTeamRowViewModel(Long id, String name, String email, boolean lead, boolean invite, Long inviteId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lead = lead;
        this.invite = invite;
        this.inviteId = inviteId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isInvite() {
        return invite;
    }

    public Long getInviteId() {
        return inviteId;
    }
}
