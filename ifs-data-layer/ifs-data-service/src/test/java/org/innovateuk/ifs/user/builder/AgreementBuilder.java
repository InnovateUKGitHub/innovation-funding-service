package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.domain.Agreement;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

/**
 * Builder for {@link Agreement}s.
 */
public class AgreementBuilder extends BaseBuilder<Agreement, AgreementBuilder> {

    private AgreementBuilder(List<BiConsumer<Integer, Agreement>> multiActions) {
        super(multiActions);
    }

    public static AgreementBuilder newAgreement() {
        return new AgreementBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AgreementBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Agreement>> actions) {
        return new AgreementBuilder(actions);
    }

    public AgreementBuilder withId(Long... ids) {
        return withArray((id, agreement) -> setField("id", id, agreement), ids);
    }

    public AgreementBuilder withCurrent(Boolean... currents) {
        return withArray((current, agreement) -> setField("current", current, agreement), currents);
    }

    public AgreementBuilder withText(String... texts) {
        return withArray((text, agreement) -> setField("text", text, agreement), texts);
    }

    @Override
    protected Agreement createInitial() {
        return createDefault(Agreement.class);
    }
}
