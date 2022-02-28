package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.EDIStatus;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ApplicationTeamRowViewModel {

    private final Long id;
    private final String name;
    private final String email;
    private final boolean lead;
    private final boolean invite;
    private final ZonedDateTime pendingSince;
    private final Long inviteId;
    private EDIStatus ediStatus;

    public static ApplicationTeamRowViewModel fromProcessRole(ProcessRoleResource processRole, Long inviteId, EDIStatus ediStatus) {
        return new ApplicationTeamRowViewModel(processRole.getUser(), processRole.getUserName(), processRole.getUserEmail(),
                processRole.getRole().isLeadApplicant(), inviteId, ediStatus);
    }

    public static ApplicationTeamRowViewModel fromInvite(ApplicationInviteResource invite) {
        return new ApplicationTeamRowViewModel(invite.getId(), invite.getName(), invite.getEmail(), invite.getSentOn());
    }

    private ApplicationTeamRowViewModel(Long id, String name, String email, boolean lead, Long inviteId, EDIStatus ediStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lead = lead;
        this.invite = false;
        this.pendingSince = null;
        this.inviteId = inviteId;
        this.ediStatus = ediStatus;
    }

    private ApplicationTeamRowViewModel(Long id, String name, String email, ZonedDateTime pendingSince) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lead = false;
        this.invite = true;
        this.pendingSince = pendingSince;
        this.inviteId = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return invite
                ? String.format("%s (pending for %d days)", name, getDaysPending())
                : name;
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

    private long getDaysPending() {
        return ChronoUnit.DAYS.between(pendingSince, ZonedDateTime.now());
    }

    public String getEdiStatusDisplayName() {
        if (ediStatus == null) {
            return EDIStatus.INCOMPLETE.getDisplayName();
        }
        return ediStatus.getDisplayName();
    }


    public EDIStatus getEdiStatus() {
        return ediStatus;
    }

    public void setEdiStatus(EDIStatus ediStatus) {
       ediStatus = ediStatus;
    }

    @JsonIgnore
    public boolean isEdiStatusComplete() {
        return ediStatus != null ? EDIStatus.COMPLETE == ediStatus : false; }

}
