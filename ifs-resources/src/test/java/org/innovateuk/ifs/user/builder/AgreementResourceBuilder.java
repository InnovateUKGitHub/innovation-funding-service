package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.AgreementResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

/**
 * Builder for {@link AgreementResource}.
 */
public class AgreementResourceBuilder extends BaseBuilder<AgreementResource, AgreementResourceBuilder> {
    private AgreementResourceBuilder(List<BiConsumer<Integer, AgreementResource>> multiActions) {
        super(multiActions);
    }

    public static AgreementResourceBuilder newAgreementResource() {
        return new AgreementResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AgreementResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AgreementResource>> actions) {
        return new AgreementResourceBuilder(actions);
    }

    public AgreementResourceBuilder withId(Long... ids) {
        return withArray((id, agreement) -> setField("id", id, agreement), ids);
    }

    public AgreementResourceBuilder withCurrent(Boolean... currents) {
        return withArray((current, agreement) -> setField("current", current, agreement), currents);
    }

    public AgreementResourceBuilder withText(String... texts) {
        return withArray((text, agreement) -> setField("text", text, agreement), texts);
    }

    @Override
    protected AgreementResource createInitial() {
        return createDefault(AgreementResource.class);
    }
}
