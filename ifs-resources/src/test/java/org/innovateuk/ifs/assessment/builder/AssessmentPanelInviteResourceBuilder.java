package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link AssessmentPanelInviteResource}
 */
public class AssessmentPanelInviteResourceBuilder extends BaseBuilder<AssessmentPanelInviteResource, AssessmentPanelInviteResourceBuilder> {

    private AssessmentPanelInviteResourceBuilder(List<BiConsumer<Integer, AssessmentPanelInviteResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentPanelInviteResourceBuilder newAssessmentPanelInviteResource() {
        return new AssessmentPanelInviteResourceBuilder(emptyList());
    }

    public AssessmentPanelInviteResourceBuilder withIds(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentPanelInviteResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public AssessmentPanelInviteResourceBuilder withCompetitionId(Long... ids) {
        return withArraySetFieldByReflection("competitionId", ids);
    }

    public AssessmentPanelInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public AssessmentPanelInviteResourceBuilder withHash(String... hashes) {
        return withArraySetFieldByReflection("hash", hashes);
    }

    public AssessmentPanelInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    @Override
    protected AssessmentPanelInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelInviteResource>> actions) {
        return new AssessmentPanelInviteResourceBuilder(actions);
    }

    @Override
    protected AssessmentPanelInviteResource createInitial() {
        return new AssessmentPanelInviteResource();
    }
}
