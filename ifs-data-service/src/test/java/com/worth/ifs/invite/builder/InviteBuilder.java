package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;

public class InviteBuilder extends BaseBuilder<ApplicationInvite, InviteBuilder> {

    private InviteBuilder(List<BiConsumer<Integer, ApplicationInvite>> multiActions) {
        super(multiActions);
    }

    public static InviteBuilder newInvite() {
        return new InviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationInvite>> actions) {
        return new InviteBuilder(actions);
    }

    public InviteBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public InviteBuilder withApplication(Application... applications) {
        return withArray((application, invite) -> invite.setTarget(application), applications);
    }

    public InviteBuilder withInviteOrganisation(InviteOrganisation... organisations) {
        return withArray((organisation, invite) -> invite.setOwner(organisation), organisations);
    }

    @Override
    public void postProcess(int index, ApplicationInvite invite) {

        // add back-refs to InviteOrganisations
        InviteOrganisation inviteOrganisation = invite.getOwner();
        if (inviteOrganisation != null && !simpleMap(inviteOrganisation.getInvites(), Invite::getId).contains(invite.getId())) {
            inviteOrganisation.getInvites().add(invite);
        }
    }

    @Override
    protected ApplicationInvite createInitial() {
        return new ApplicationInvite();
    }
}
