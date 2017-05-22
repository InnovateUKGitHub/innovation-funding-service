package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorInviteToSendResourceBuilder extends BaseBuilder<AssessorInvitesToSendResource, AssessorInviteToSendResourceBuilder> {

    private AssessorInviteToSendResourceBuilder(List<BiConsumer<Integer, AssessorInvitesToSendResource>> actions) {
        super(actions);
    }

    @Override
    protected AssessorInviteToSendResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorInvitesToSendResource>> actions) {
        return new AssessorInviteToSendResourceBuilder(actions);
    }

    @Override
    protected AssessorInvitesToSendResource createInitial() {
        return new AssessorInvitesToSendResource();
    }

    public static AssessorInviteToSendResourceBuilder newAssessorInviteToSendResource() {
        return new AssessorInviteToSendResourceBuilder(emptyList());
    }

    public AssessorInviteToSendResourceBuilder withRecipient(String... value) {
        return withArraySetFieldByReflection("recipient", value);
    }

    public AssessorInviteToSendResourceBuilder withCompetitionId(Long... value) {
        return withArraySetFieldByReflection("competitionId", value);
    }

    public AssessorInviteToSendResourceBuilder withCompetitionName(String... value) {
        return withArraySetFieldByReflection("competitionName", value);
    }

    public AssessorInviteToSendResourceBuilder withContent(String... value) {
        return withArraySetFieldByReflection("content", value);
    }

}
