package org.innovateuk.ifs.assessment.panel.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInviteRejectOutcome;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentPanelApplicationInviteRejectOutcomeBuilder extends BaseBuilder<AssessmentPanelApplicationInviteRejectOutcome, AssessmentPanelApplicationInviteRejectOutcomeBuilder> {

    private AssessmentPanelApplicationInviteRejectOutcomeBuilder(List<BiConsumer<Integer, AssessmentPanelApplicationInviteRejectOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentPanelApplicationInviteRejectOutcomeBuilder newAssessmentPanelApplicationInviteRejectOutcome() {
        return new AssessmentPanelApplicationInviteRejectOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentPanelApplicationInviteRejectOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelApplicationInviteRejectOutcome>> actions) {
        return new AssessmentPanelApplicationInviteRejectOutcomeBuilder(actions);
    }

    @Override
    protected AssessmentPanelApplicationInviteRejectOutcome createInitial() {
        return new AssessmentPanelApplicationInviteRejectOutcome();
    }

    public AssessmentPanelApplicationInviteRejectOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentPanelApplicationInviteRejectOutcomeBuilder withRejectionComment(String... rejectionComments) {
        return withArray((rejectionComment, assessmentRejectOutcome) -> assessmentRejectOutcome.setRejectionComment(rejectionComment), rejectionComments);
    }
}