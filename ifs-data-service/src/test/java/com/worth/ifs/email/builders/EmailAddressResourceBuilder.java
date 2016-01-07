package com.worth.ifs.email.builders;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.email.resource.EmailAddressResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class EmailAddressResourceBuilder extends BaseBuilder<EmailAddressResource, EmailAddressResourceBuilder> {

    private EmailAddressResourceBuilder(List<BiConsumer<Integer, EmailAddressResource>> multiActions) {
        super(multiActions);
    }

    public static EmailAddressResourceBuilder newEmailAddressResource() {
        return new EmailAddressResourceBuilder(emptyList());
    }

    @Override
    protected EmailAddressResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EmailAddressResource>> actions) {
        return new EmailAddressResourceBuilder(actions);
    }

    @Override
    protected EmailAddressResource createInitial() {
        return new EmailAddressResource();
    }

    public EmailAddressResourceBuilder withEmail(String... emails) {
        return withArray((email, emailResource) -> setField("emailAddress", email, emailResource), emails);
    }
}
