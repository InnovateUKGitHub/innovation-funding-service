package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorInvitesToSendResourceBuilder extends BaseBuilder<AssessorInvitesToSendResource, AssessorInvitesToSendResourceBuilder> {

    private AssessorInvitesToSendResourceBuilder(List<BiConsumer<Integer, AssessorInvitesToSendResource>> actions) {
        super(actions);
    }

    @Override
    protected AssessorInvitesToSendResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorInvitesToSendResource>> actions) {
        return new AssessorInvitesToSendResourceBuilder(actions);
    }

    @Override
    protected AssessorInvitesToSendResource createInitial() {
        return new AssessorInvitesToSendResource();
    }

    public static AssessorInvitesToSendResourceBuilder newAssessorInvitesToSendResource() {
        return new AssessorInvitesToSendResourceBuilder(emptyList());
    }

    public AssessorInvitesToSendResourceBuilder withRecipients(List<String>... value) {
        return withArraySetFieldByReflection("recipients", value);
    }

    public AssessorInvitesToSendResourceBuilder withCompetitionId(Long... value) {
        return withArraySetFieldByReflection("competitionId", value);
    }

    public AssessorInvitesToSendResourceBuilder withCompetitionName(String... value) {
        return withArraySetFieldByReflection("competitionName", value);
    }

    public AssessorInvitesToSendResourceBuilder withContent(String... value) {
        return withArraySetFieldByReflection("content", value);
    }

}
