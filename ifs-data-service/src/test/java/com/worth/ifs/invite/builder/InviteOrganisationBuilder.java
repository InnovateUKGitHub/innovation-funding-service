package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class InviteOrganisationBuilder extends BaseBuilder<InviteOrganisation, InviteOrganisationBuilder> {

    private InviteOrganisationBuilder(List<BiConsumer<Integer, InviteOrganisation>> multiActions) {
        super(multiActions);
    }

    public static InviteOrganisationBuilder newInviteOrganisation() {
        return new InviteOrganisationBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteOrganisationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InviteOrganisation>> actions) {
        return new InviteOrganisationBuilder(actions);
    }

    @Override
    protected InviteOrganisation createInitial() {
        return new InviteOrganisation();
    }

    public InviteOrganisationBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, inviteOrganisation) -> inviteOrganisation.setOrganisation(organisation), organisations);
    }

}
