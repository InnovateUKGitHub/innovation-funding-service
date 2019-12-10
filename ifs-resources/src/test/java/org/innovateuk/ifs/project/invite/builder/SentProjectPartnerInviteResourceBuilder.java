package org.innovateuk.ifs.project.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class SentProjectPartnerInviteResourceBuilder extends BaseBuilder<SentProjectPartnerInviteResource, SentProjectPartnerInviteResourceBuilder> {

    private SentProjectPartnerInviteResourceBuilder(List<BiConsumer<Integer, SentProjectPartnerInviteResource>> multiActions) {
        super(multiActions);
    }

    public static SentProjectPartnerInviteResourceBuilder newSentProjectPartnerInviteResource() {
        return new SentProjectPartnerInviteResourceBuilder(emptyList());
    }

    @Override
    protected SentProjectPartnerInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SentProjectPartnerInviteResource>> actions) {
        return new SentProjectPartnerInviteResourceBuilder(actions);
    }

    @Override
    protected SentProjectPartnerInviteResource createInitial() {
        return newInstance(SentProjectPartnerInviteResource.class);
    }

    public SentProjectPartnerInviteResourceBuilder withOrganisationName(String... organisationNames) {
        return withArray((organisationName, sentProjectPartnerInviteResource) -> setField("organisationName", organisationName, sentProjectPartnerInviteResource), organisationNames);
    }

    public SentProjectPartnerInviteResourceBuilder withStatus(InviteStatus... inviteStatuses) {
        return withArray((inviteStatus, sentProjectPartnerInviteResource) -> setField("status", inviteStatus, sentProjectPartnerInviteResource), inviteStatuses);
    }

    public SentProjectPartnerInviteResourceBuilder withUserName(String... userNames) {
        return withArray((userName, sentProjectPartnerInviteResource) -> setField("userName", userName, sentProjectPartnerInviteResource), userNames);
    }

    public SentProjectPartnerInviteResourceBuilder withEmail(String... emails) {
        return withArray((email, sentProjectPartnerInviteResource) -> setField("email", email, sentProjectPartnerInviteResource), emails);
    }

    public SentProjectPartnerInviteResourceBuilder withSentOn(ZonedDateTime... sentOns) {
        return withArray((sentOn, sentProjectPartnerInviteResource) -> setField("sentOn", sentOn, sentProjectPartnerInviteResource), sentOns);
    }

}
