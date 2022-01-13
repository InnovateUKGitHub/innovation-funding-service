package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.SimpleUserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class SimpleUserResourceBuilder extends BaseBuilder<SimpleUserResource, SimpleUserResourceBuilder> {

    private SimpleUserResourceBuilder(List<BiConsumer<Integer, SimpleUserResource>> multiActions) {
        super(multiActions);
    }

    public static SimpleUserResourceBuilder newSimpleUserResource() {
        return new SimpleUserResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected SimpleUserResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SimpleUserResource>> actions) {
        return new SimpleUserResourceBuilder(actions);
    }

    @Override
    protected SimpleUserResource createInitial() {
        return new SimpleUserResource();
    }

    public SimpleUserResourceBuilder withId(Long... ids) {
        return withArray((id, user) -> setField("id", id, user), ids);
    }

    public SimpleUserResourceBuilder withFirstName(String... firstNames) {
        return withArray((firstName, user) -> setField("firstName", firstName, user), firstNames);
    }

    public SimpleUserResourceBuilder withLastName(String... lastNames) {
        return withArray((lastName, user) -> setField("lastName", lastName, user), lastNames);
    }

    public SimpleUserResourceBuilder withEmail(String... emails) {
        return withArray((email, user) -> setField("email", email, user), emails);
    }

}
