package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.EDIStatus;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ApplicationTeamUserReadOnlyViewModel {
    private final String name;
    private final String email;
    private final String phone;
    private final boolean lead;
    private final boolean invite;
    private final ZonedDateTime pendingSince;
    private final EDIStatus ediStatus;

    public ApplicationTeamUserReadOnlyViewModel(String name, String email, String phone, boolean lead, boolean invite,
                                                ZonedDateTime pendingSince, EDIStatus ediStatus) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.lead = lead;
        this.invite = invite;
        this.pendingSince = pendingSince;
        this.ediStatus = ediStatus;
    }

    public static ApplicationTeamUserReadOnlyViewModel fromProcessRole(ProcessRoleResource pr, String phone, EDIStatus ediStatus) {
        return new ApplicationTeamUserReadOnlyViewModel(pr.getUserName(), pr.getUserEmail(), phone, pr.getRole().isLeadApplicant(),
                false, null, ediStatus);
    }

    public static ApplicationTeamUserReadOnlyViewModel fromInvite(ApplicationInviteResource invite) {
        return new ApplicationTeamUserReadOnlyViewModel(invite.getName(), invite.getEmail(), null, false,
                true, invite.getSentOn(), null);
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
                ? String.format("%s (pending for %d days)", name, getDaysPending())
                : name;
    }

    public String getPhone() {
        return phone;
    }

    private long getDaysPending() {
        return ChronoUnit.DAYS.between(pendingSince, ZonedDateTime.now());
    }

    public boolean isEdiCompleted() {
        return ediStatus != null
            && EDIStatus.COMPLETE == ediStatus;
    }

    public String getEdiStatus() {
        return ediStatus == null
                ? EDIStatus.INCOMPLETE.getDisplayName()
                : ediStatus.getDisplayName();
    }
}
