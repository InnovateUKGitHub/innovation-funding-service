package com.worth.ifs.email.builders;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.email.resource.EmailAddress;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class EmailAddressResourceBuilder extends BaseBuilder<EmailAddress, EmailAddressResourceBuilder> {

    private EmailAddressResourceBuilder(List<BiConsumer<Integer, EmailAddress>> multiActions) {
        super(multiActions);
    }

    public static EmailAddressResourceBuilder newEmailAddressResource() {
        return new EmailAddressResourceBuilder(emptyList());
    }

    @Override
    protected EmailAddressResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EmailAddress>> actions) {
        return new EmailAddressResourceBuilder(actions);
    }

    @Override
    protected EmailAddress createInitial() {
        return new EmailAddress();
    }

    public EmailAddressResourceBuilder withEmail(String... emails) {
        return withArray((email, emailResource) -> setField("emailAddress", email, emailResource), emails);
    }
}
