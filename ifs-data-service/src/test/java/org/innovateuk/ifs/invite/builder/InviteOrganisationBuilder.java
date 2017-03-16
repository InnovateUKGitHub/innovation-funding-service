package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public InviteOrganisationBuilder withOrganisationName(String... organisationNames) {
        return withArraySetFieldByReflection("organisationName", organisationNames);
    }

    public InviteOrganisationBuilder withOrganisation(Organisation... organisations) {
        return withArraySetFieldByReflection("organisation", organisations);
    }

    public InviteOrganisationBuilder withInvites(List<ApplicationInvite>... invites) {
        return withArraySetFieldByReflection("invites", invites);
    }
}
