package org.innovateuk.ifs.assessment.review.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.builder.BaseInviteBuilder;
import org.innovateuk.ifs.invite.domain.competition.AssessmentReviewPanelInvite;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentPanelInviteBuilder extends BaseInviteBuilder<Competition, AssessmentReviewPanelInvite, AssessmentPanelInviteBuilder> {
    private AssessmentPanelInviteBuilder(List<BiConsumer<Integer, AssessmentReviewPanelInvite>> multiActions) {
        super(multiActions);
    }

    public static AssessmentPanelInviteBuilder newAssessmentPanelInvite() {
        return new AssessmentPanelInviteBuilder(emptyList()).with(uniqueIds());
    }

    public static AssessmentPanelInviteBuilder newAssessmentPanelInviteWithoutId() {
        return new AssessmentPanelInviteBuilder(emptyList());
    }

    @Override
    protected AssessmentPanelInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentReviewPanelInvite>> actions) {
        return new AssessmentPanelInviteBuilder(actions);
    }

    public AssessmentPanelInviteBuilder withCompetition(Competition... competitions) {
        return withTarget(competitions);
    }

    public AssessmentPanelInviteBuilder withCompetition(Builder<Competition, ?> competition) {
        return withCompetition(competition.build());
    }

    public AssessmentPanelInviteBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public AssessmentPanelInviteBuilder withSentBy(User... users) {
        return  withArraySetFieldByReflection("sentBy", users);
    }

    public AssessmentPanelInviteBuilder withSentOn(ZonedDateTime... dates) {
        return  withArraySetFieldByReflection("sentOn", dates);
    }
    @Override
    protected AssessmentReviewPanelInvite createInitial() {
        return new AssessmentReviewPanelInvite();
    }
}
