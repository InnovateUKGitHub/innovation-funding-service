package org.innovateuk.ifs.eugrant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.eugrant.EuContactResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuContactResourceBuilder extends BaseBuilder<EuContactResource, EuContactResourceBuilder> {

    private EuContactResourceBuilder(List<BiConsumer<Integer, EuContactResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static EuContactResourceBuilder newEuContactResource() {
        return new EuContactResourceBuilder(emptyList());
    }

    @Override
    protected EuContactResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuContactResource>> actions) {
        return new EuContactResourceBuilder(actions);
    }

    @Override
    protected EuContactResource createInitial() {
        return new EuContactResource();
    }

    public EuContactResourceBuilder withName(String... names) {
        return withArray((name, contact) -> contact.setName(name), names);
    }

    public EuContactResourceBuilder withJobTitle(String... jobTitles) {
        return withArray((jobTitle, contact) -> contact.setJobTitle(jobTitle), jobTitles);
    }

    public EuContactResourceBuilder withEmail(String... emails) {
        return withArray((email, contact) -> contact.setEmail(email), emails);
    }

    public EuContactResourceBuilder withTelephone(String... telephones) {
        return withArray((telephone, contact) -> contact.setTelephone(telephone), telephones);
    }

    public EuContactResourceBuilder withNotified(Boolean... notifiedFlags) {
        return withArray((notifiedFlag, contact) -> contact.setNotified(notifiedFlag), notifiedFlags);
    }
}