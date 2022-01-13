package org.innovateuk.ifs.form.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class MultipleChoiceOptionResourceBuilder extends BaseBuilder<MultipleChoiceOptionResource, MultipleChoiceOptionResourceBuilder> {

    private MultipleChoiceOptionResourceBuilder(List<BiConsumer<Integer, MultipleChoiceOptionResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected MultipleChoiceOptionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, MultipleChoiceOptionResource>> actions) {
        return new MultipleChoiceOptionResourceBuilder(actions);
    }

    @Override
    protected MultipleChoiceOptionResource createInitial() {
        return new MultipleChoiceOptionResource();
    }

    public static MultipleChoiceOptionResourceBuilder newMultipleChoiceOptionResource() {
        return new MultipleChoiceOptionResourceBuilder(emptyList())
                .with(uniqueIds());
    }

    public MultipleChoiceOptionResourceBuilder withId(Long... ids) {
        return withArray((id, formInput) -> formInput.setId(id), ids);
    }

    public MultipleChoiceOptionResourceBuilder withText(String... texts) {
        return withArray((text, formInput) -> formInput.setText(text), texts);
    }
}