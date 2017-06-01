package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationInviteBuilder extends BaseInviteBuilder<Application, ApplicationInvite, ApplicationInviteBuilder> {

    private ApplicationInviteBuilder(List<BiConsumer<Integer, ApplicationInvite>> multiActions) {
        super(multiActions);
    }

    public static ApplicationInviteBuilder newApplicationInvite() {
        return new ApplicationInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationInvite>> actions) {
        return new ApplicationInviteBuilder(actions);
    }

    public ApplicationInviteBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public ApplicationInviteBuilder withApplication(Application... applications) {
        return withTarget(applications);
    }

    public ApplicationInviteBuilder withInviteOrganisation(InviteOrganisation... organisations) {
        return withArray((organisation, invite) -> invite.setInviteOrganisation(organisation), organisations);
    }

    @Override
    public void postProcess(int index, ApplicationInvite invite) {

        // add back-refs to InviteOrganisations
        InviteOrganisation inviteOrganisation = invite.getInviteOrganisation();
        if (inviteOrganisation != null) {
            inviteOrganisation.getInvites().add(invite);
        }
    }

    @Override
    protected ApplicationInvite createInitial() {
        return new ApplicationInvite();
    }
}
