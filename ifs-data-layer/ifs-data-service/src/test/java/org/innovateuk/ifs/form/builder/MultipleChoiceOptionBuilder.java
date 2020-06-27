package org.innovateuk.ifs.form.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

public class MultipleChoiceOptionBuilder extends BaseBuilder<MultipleChoiceOption, MultipleChoiceOptionBuilder> {

    private MultipleChoiceOptionBuilder(List<BiConsumer<Integer, MultipleChoiceOption>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected MultipleChoiceOptionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, MultipleChoiceOption>> actions) {
        return new MultipleChoiceOptionBuilder(actions);
    }

    public static MultipleChoiceOptionBuilder newMultipleChoiceOption() {
        return new MultipleChoiceOptionBuilder(emptyList())
                .with(uniqueIds());
    }

    public MultipleChoiceOptionBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public MultipleChoiceOptionBuilder withFormInput(FormInput formInput) {
        return with(guidanceRow -> setField("formInput", formInput, guidanceRow));
    }

    public MultipleChoiceOptionBuilder withText(String... text) {
        return withArraySetFieldByReflection("text", text);
    }

    @Override
    protected MultipleChoiceOption createInitial() {
        return createDefault(MultipleChoiceOption.class);
    }
}
