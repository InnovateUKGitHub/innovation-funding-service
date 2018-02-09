package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelInviteResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link AssessmentReviewPanelInviteResource}
 */
public class AssessmentReviewPanelInviteResourceBuilder extends BaseBuilder<AssessmentReviewPanelInviteResource, AssessmentReviewPanelInviteResourceBuilder> {

    private AssessmentReviewPanelInviteResourceBuilder(List<BiConsumer<Integer, AssessmentReviewPanelInviteResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewPanelInviteResourceBuilder newAssessmentReviewPanelInviteResource() {
        return new AssessmentReviewPanelInviteResourceBuilder(emptyList());
    }

    public AssessmentReviewPanelInviteResourceBuilder withIds(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentReviewPanelInviteResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public AssessmentReviewPanelInviteResourceBuilder withCompetitionId(Long... ids) {
        return withArraySetFieldByReflection("competitionId", ids);
    }

    public AssessmentReviewPanelInviteResourceBuilder withUserId(Long... ids) {
        return withArraySetFieldByReflection("userId", ids);
    }

    public AssessmentReviewPanelInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public AssessmentReviewPanelInviteResourceBuilder withInviteHash(String... hashes) {
        return withArraySetFieldByReflection("hash", hashes);
    }

    public AssessmentReviewPanelInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public AssessmentReviewPanelInviteResourceBuilder withPanelDate(ZonedDateTime... panelDates) {
        return withArraySetFieldByReflection("panelDate", panelDates);
    }

    @Override
    protected AssessmentReviewPanelInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentReviewPanelInviteResource>> actions) {
        return new AssessmentReviewPanelInviteResourceBuilder(actions);
    }

    @Override
    protected AssessmentReviewPanelInviteResource createInitial() {
        return new AssessmentReviewPanelInviteResource();
    }
}
