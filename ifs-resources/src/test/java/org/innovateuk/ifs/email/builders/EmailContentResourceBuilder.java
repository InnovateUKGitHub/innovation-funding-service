package org.innovateuk.ifs.email.builders;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.email.resource.EmailContent;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class EmailContentResourceBuilder extends BaseBuilder<EmailContent, EmailContentResourceBuilder> {

    private EmailContentResourceBuilder(List<BiConsumer<Integer, EmailContent>> multiActions) {
        super(multiActions);
    }

    public static EmailContentResourceBuilder newEmailContentResource() {
        return new EmailContentResourceBuilder(emptyList());
    }

    @Override
    protected EmailContentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EmailContent>> actions) {
        return new EmailContentResourceBuilder(actions);
    }

    @Override
    protected EmailContent createInitial() {
        return new EmailContent();
    }

    public EmailContentResourceBuilder withSubject(String... subjects) {
        return withArray((subject, contentResource) -> setField("subject", subject, contentResource), subjects);
    }

    public EmailContentResourceBuilder withPlainText(String... plainTexts) {
        return withArray((plainText, contentResource) -> setField("plainText", plainText, contentResource), plainTexts);
    }

    public EmailContentResourceBuilder withHtmlText(String... htmlTexts) {
        return withArray((htmlText, contentResource) -> setField("htmlText", htmlText, contentResource), htmlTexts);
    }
}
