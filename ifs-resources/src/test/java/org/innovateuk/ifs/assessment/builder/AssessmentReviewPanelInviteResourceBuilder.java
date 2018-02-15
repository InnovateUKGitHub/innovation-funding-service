package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link ReviewInviteResource}
 */
public class AssessmentReviewPanelInviteResourceBuilder extends BaseBuilder<ReviewInviteResource, AssessmentReviewPanelInviteResourceBuilder> {

    private AssessmentReviewPanelInviteResourceBuilder(List<BiConsumer<Integer, ReviewInviteResource>> multiActions) {
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
    protected AssessmentReviewPanelInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewInviteResource>> actions) {
        return new AssessmentReviewPanelInviteResourceBuilder(actions);
    }

    @Override
    protected ReviewInviteResource createInitial() {
        return new ReviewInviteResource();
    }
}
