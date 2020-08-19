package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationKtaInviteResourceBuilder  extends BaseBuilder<ApplicationKtaInviteResource, ApplicationKtaInviteResourceBuilder> {

    private ApplicationKtaInviteResourceBuilder(List<BiConsumer<Integer, ApplicationKtaInviteResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationKtaInviteResourceBuilder newApplicationKtaInviteResource() {
        return new ApplicationKtaInviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationKtaInviteResource createInitial() {
        return new ApplicationKtaInviteResource();
    }

    @Override
    protected ApplicationKtaInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationKtaInviteResource>> actions) {
        return new ApplicationKtaInviteResourceBuilder(actions);
    }

    public ApplicationKtaInviteResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ApplicationKtaInviteResourceBuilder withApplication(Long... applications) {
        return withArraySetFieldByReflection("application", applications);
    }

    public ApplicationKtaInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public ApplicationKtaInviteResourceBuilder withSentOn(ZonedDateTime... sentOn) {
        return withArraySetFieldByReflection("sentOn", sentOn);
    }

    public ApplicationKtaInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public ApplicationKtaInviteResourceBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    @Override
    protected void postProcess(int index, ApplicationKtaInviteResource instance) {
        super.postProcess(index, instance);
    }
}
