package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionInviteStatisticsResourceBuilder extends BaseBuilder<CompetitionInviteStatisticsResource, CompetitionInviteStatisticsResourceBuilder> {

    private CompetitionInviteStatisticsResourceBuilder(List<BiConsumer<Integer, CompetitionInviteStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected CompetitionInviteStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionInviteStatisticsResource>> actions) {
        return new CompetitionInviteStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionInviteStatisticsResource createInitial() {
        return new CompetitionInviteStatisticsResource();
    }

    public static CompetitionInviteStatisticsResourceBuilder newCompetitionInviteStatisticsResource() {
        return new CompetitionInviteStatisticsResourceBuilder(emptyList());
    }

    public CompetitionInviteStatisticsResourceBuilder withInvited(Integer... inviteds) {
        return withArraySetFieldByReflection("invited", inviteds);
    }

    public CompetitionInviteStatisticsResourceBuilder withAccepted(Integer... accepteds) {
        return withArraySetFieldByReflection("accepted", accepteds);
    }

    public CompetitionInviteStatisticsResourceBuilder withDeclined(Integer... declineds) {
        return withArraySetFieldByReflection("declined", declineds);
    }

    public CompetitionInviteStatisticsResourceBuilder withInviteList(Integer... inviteLists) {
        return withArraySetFieldByReflection("inviteList", inviteLists);
    }

}
