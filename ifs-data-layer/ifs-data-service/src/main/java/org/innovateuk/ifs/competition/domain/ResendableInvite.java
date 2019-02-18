package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;

public interface ResendableInvite<T, I extends Invite<T, I>> {

    I sendOrResend(User sentBy, ZonedDateTime sentOn);
}