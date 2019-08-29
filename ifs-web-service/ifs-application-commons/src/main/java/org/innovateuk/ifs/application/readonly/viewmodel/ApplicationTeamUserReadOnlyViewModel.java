package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ApplicationTeamUserReadOnlyViewModel {
    private final String name;
    private final String email;
    private final boolean lead;
    private final boolean invite;
    private final ZonedDateTime pendingSince;

    public ApplicationTeamUserReadOnlyViewModel(String name, String email, boolean lead, boolean invite, ZonedDateTime pendingSince) {
        this.name = name;
        this.email = email;
        this.lead = lead;
        this.invite = invite;
        this.pendingSince = pendingSince;
    }

    public static ApplicationTeamUserReadOnlyViewModel fromProcessRole(ProcessRoleResource pr) {
        return new ApplicationTeamUserReadOnlyViewModel(pr.getUserName(), pr.getUserEmail(), pr.getRole().isLeadApplicant(), false, null);
    }

    public static ApplicationTeamUserReadOnlyViewModel fromInvite(ApplicationInviteResource invite) {
        return new ApplicationTeamUserReadOnlyViewModel(invite.getName(), invite.getEmail(),false, true, invite.getSentOn());
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

    public String getName() {
        return invite
                ? String.format("%s (Pending for %d days)", name, getDaysPending())
                : name;
    }
    private long getDaysPending() {
        return ChronoUnit.DAYS.between(pendingSince, ZonedDateTime.now());
    }
}
