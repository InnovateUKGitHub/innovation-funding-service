package org.innovateuk.ifs.eugrant.domain;

import org.innovateuk.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuContactBuilder extends BaseBuilder<EuContact, EuContactBuilder> {

    private EuContactBuilder(List<BiConsumer<Integer, EuContact>> multiActions) {
        super(multiActions);
    }

    public static EuContactBuilder newEuContact() {
        return new EuContactBuilder(emptyList());
    }

    @Override
    protected EuContactBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuContact>> actions) {
        return new EuContactBuilder(actions);
    }

    @Override
    protected EuContact createInitial() {
        return new EuContact();
    }

    public EuContactBuilder withName(String... names) {
        return withArray((name, contact) -> contact.setName(name), names);
    }

    public EuContactBuilder withJobTitle(String... jobTitles) {
        return withArray((jobTitle, contact) -> contact.setJobTitle(jobTitle), jobTitles);
    }

    public EuContactBuilder withEmail(String... emails) {
        return withArray((email, contact) -> contact.setEmail(email), emails);
    }

    public EuContactBuilder withTelephone(String... telephones) {
        return withArray((telephone, contact) -> contact.setTelephone(telephone), telephones);
    }
}
