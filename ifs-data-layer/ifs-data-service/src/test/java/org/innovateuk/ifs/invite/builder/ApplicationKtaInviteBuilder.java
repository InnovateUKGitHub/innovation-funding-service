package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationKtaInviteBuilder extends BaseInviteBuilder<Application, ApplicationKtaInvite, ApplicationKtaInviteBuilder> {

    private ApplicationKtaInviteBuilder(List<BiConsumer<Integer, ApplicationKtaInvite>> multiActions) {
        super(multiActions);
    }

    public static ApplicationKtaInviteBuilder newApplicationKtaInvite() {
        return new ApplicationKtaInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationKtaInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationKtaInvite>> actions) {
        return new ApplicationKtaInviteBuilder(actions);
    }

    public ApplicationKtaInviteBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public ApplicationKtaInviteBuilder withApplication(Application... applications) {
        return withTarget(applications);
    }

    @Override
    public void postProcess(int index, ApplicationKtaInvite invite) {
    }

    @Override
    protected ApplicationKtaInvite createInitial() {
        return new ApplicationKtaInvite();
    }
}