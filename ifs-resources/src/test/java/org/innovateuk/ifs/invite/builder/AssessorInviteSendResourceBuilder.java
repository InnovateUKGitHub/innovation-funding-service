package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorInviteSendResourceBuilder extends BaseBuilder<AssessorInviteSendResource, AssessorInviteSendResourceBuilder> {

    private AssessorInviteSendResourceBuilder(List<BiConsumer<Integer, AssessorInviteSendResource>> actions) {
        super(actions);
    }

    @Override
    protected AssessorInviteSendResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorInviteSendResource>> actions) {
        return new AssessorInviteSendResourceBuilder(actions);
    }

    @Override
    protected AssessorInviteSendResource createInitial() {
        return new AssessorInviteSendResource();
    }

    public static AssessorInviteSendResourceBuilder newAssessorInviteSendResource() {
        return new AssessorInviteSendResourceBuilder(emptyList());
    }

    public AssessorInviteSendResourceBuilder withSubject(String... value) {
        return withArraySetFieldByReflection("subject", value);
    }

    public AssessorInviteSendResourceBuilder withContent(String... value) {
        return withArraySetFieldByReflection("content", value);
    }
}