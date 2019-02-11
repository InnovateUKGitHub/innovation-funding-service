package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.user.domain.ProcessActivity;

public interface InvitedParticipant<P extends ProcessActivity, I extends Invite<P,I>, R extends ParticipantRole> {

    public abstract I getInvite();
}
