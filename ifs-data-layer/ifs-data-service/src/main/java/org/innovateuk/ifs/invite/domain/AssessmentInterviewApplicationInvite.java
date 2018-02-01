package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.time.ZonedDateTime;


// TODO remove this as we're using a process instead
@Entity
@DiscriminatorValue("ASSESSMENT_INTERVIEW_PANEL")
public class AssessmentInterviewApplicationInvite extends Invite<Application, AssessmentInterviewApplicationInvite> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application application;

//    private InviteMessage inviteMessage;

    public AssessmentInterviewApplicationInvite() {
    }

    public AssessmentInterviewApplicationInvite(Application application, String message, User sentBy, ZonedDateTime sentOn) {
        this.application = application;
        doSend(sentBy, sentOn);
    }

    @Override
    public Application getTarget() {
        return application;
    }

    @Override
    public void setTarget(Application target) {
        this.application = target;
    }
}