package org.innovateuk.ifs.assessment.panel.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInvite;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInviteRejectOutcome;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE;

public class AssessmentPanelApplicationInviteBuilder extends BaseBuilder<AssessmentPanelApplicationInvite, AssessmentPanelApplicationInviteBuilder> {

    private AssessmentPanelApplicationInviteBuilder(List<BiConsumer<Integer, AssessmentPanelApplicationInvite>> multiActions) {
        super(multiActions);
    }

    public static AssessmentPanelApplicationInviteBuilder newAssessmentPanelApplicationInvite() {
        return new AssessmentPanelApplicationInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentPanelApplicationInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelApplicationInvite>> actions) {
        return new AssessmentPanelApplicationInviteBuilder(actions);
    }

    @Override
    protected AssessmentPanelApplicationInvite createInitial() {
        return new AssessmentPanelApplicationInvite();
    }

    public AssessmentPanelApplicationInviteBuilder withId(Long... ids) {
        return withArray((id, invite) -> setField("id", id, invite), ids);
    }

    public AssessmentPanelApplicationInviteBuilder withRejection(AssessmentPanelApplicationInviteRejectOutcome... rejections) {
        return withArray((rejection, invite) -> invite.setRejection(rejection), rejections);
    }

    public AssessmentPanelApplicationInviteBuilder withTarget(Application... applications) {
        return withArray((application, invite) -> invite.setTarget(application), applications);
    }

    public AssessmentPanelApplicationInviteBuilder withParticipant(ProcessRole... participants) {
        return withArray((participant, invite) -> invite.setParticipant(participant), participants);
    }

    public AssessmentPanelApplicationInviteBuilder withState(AssessmentPanelApplicationInviteState... states) {
        return withArray((state, invite) -> invite.setActivityState(new ActivityState(ASSESSMENT_PANEL_APPLICATION_INVITE, state.getBackingState())), states);
    }
}