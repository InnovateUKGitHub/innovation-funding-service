package org.innovateuk.ifs.projectteam.viewmodel;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.String.format;

public class ProjectTeamInviteViewModel extends AbstractProjectTeamRowViewModel {
    private final boolean canResend;
    private final ZonedDateTime pendingSince;

    public ProjectTeamInviteViewModel(long id, String email, String name, ZonedDateTime pendingSince, boolean removeable, boolean canResend) {
        super(id, email, name, removeable);
        this.pendingSince = pendingSince;
        this.canResend = canResend;
    }

    @Override
    public boolean isInvite() {
        return true;
    }

    @Override
    public String getName() {
        return format("%s (pending for %d days)", super.getName(), getDaysPending());
    }

    public boolean isCanResend() { return canResend; }

    private long getDaysPending() {
        return ChronoUnit.DAYS.between(pendingSince, ZonedDateTime.now());
    }
}
