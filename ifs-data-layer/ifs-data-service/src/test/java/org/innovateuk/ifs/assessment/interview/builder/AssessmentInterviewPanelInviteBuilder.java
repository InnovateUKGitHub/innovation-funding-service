package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.builder.BaseInviteBuilder;
import org.innovateuk.ifs.invite.domain.competition.InterviewInvite;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentInterviewPanelInviteBuilder extends BaseInviteBuilder<Competition, InterviewInvite, AssessmentInterviewPanelInviteBuilder> {
    private AssessmentInterviewPanelInviteBuilder(List<BiConsumer<Integer, InterviewInvite>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewPanelInviteBuilder newAssessmentInterviewPanelInvite() {
        return new AssessmentInterviewPanelInviteBuilder(emptyList()).with(uniqueIds());
    }

    public static AssessmentInterviewPanelInviteBuilder newAssessmentInterviewPanelInviteWithoutId() {
        return new AssessmentInterviewPanelInviteBuilder(emptyList());
    }

    @Override
    protected AssessmentInterviewPanelInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewInvite>> actions) {
        return new AssessmentInterviewPanelInviteBuilder(actions);
    }

    public AssessmentInterviewPanelInviteBuilder withCompetition(Competition... competitions) {
        return withTarget(competitions);
    }

    public AssessmentInterviewPanelInviteBuilder withCompetition(Builder<Competition, ?> competition) {
        return withCompetition(competition.build());
    }

    public AssessmentInterviewPanelInviteBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public AssessmentInterviewPanelInviteBuilder withSentBy(User... users) {
        return  withArraySetFieldByReflection("sentBy", users);
    }

    public AssessmentInterviewPanelInviteBuilder withSentOn(ZonedDateTime... dates) {
        return  withArraySetFieldByReflection("sentOn", dates);
    }
    @Override
    protected InterviewInvite createInitial() {
        return new InterviewInvite();
    }
}
