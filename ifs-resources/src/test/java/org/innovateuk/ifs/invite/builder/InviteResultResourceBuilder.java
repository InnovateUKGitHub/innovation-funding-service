package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class InviteResultResourceBuilder extends BaseBuilder<InviteResultsResource, InviteResultResourceBuilder> {

    private InviteResultResourceBuilder(List<BiConsumer<Integer, InviteResultsResource>> multiActions) {
        super(multiActions);
    }

    public static InviteResultResourceBuilder newInviteResultResource() {
        return new InviteResultResourceBuilder(emptyList());
    }

    @Override
    protected InviteResultResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InviteResultsResource>> actions) {
        return new InviteResultResourceBuilder(actions);
    }

    public InviteResultResourceBuilder withId(Long... ids) {
        return withArray((id, inviteResource) -> setField("id", id, inviteResource), ids);
    }


    @Override
    protected InviteResultsResource createInitial() {
        return new InviteResultsResource();
    }
}
