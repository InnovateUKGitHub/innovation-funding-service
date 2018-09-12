package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.invite.builder.BaseInviteBuilder;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InterviewInviteBuilder extends BaseInviteBuilder<Competition, InterviewInvite, InterviewInviteBuilder> {
    private InterviewInviteBuilder(List<BiConsumer<Integer, InterviewInvite>> multiActions) {
        super(multiActions);
    }

    public static InterviewInviteBuilder newInterviewInvite() {
        return new InterviewInviteBuilder(emptyList()).with(uniqueIds());
    }

    public static InterviewInviteBuilder newInterviewInviteWithoutId() {
        return new InterviewInviteBuilder(emptyList());
    }

    @Override
    protected InterviewInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewInvite>> actions) {
        return new InterviewInviteBuilder(actions);
    }

    public InterviewInviteBuilder withCompetition(Competition... competitions) {
        return withTarget(competitions);
    }

    public InterviewInviteBuilder withCompetition(Builder<Competition, ?> competition) {
        return withCompetition(competition.build());
    }

    public InterviewInviteBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public InterviewInviteBuilder withSentBy(User... users) {
        return  withArraySetFieldByReflection("sentBy", users);
    }

    public InterviewInviteBuilder withSentOn(ZonedDateTime... dates) {
        return  withArraySetFieldByReflection("sentOn", dates);
    }
    @Override
    protected InterviewInvite createInitial() {
        return new InterviewInvite();
    }
}
