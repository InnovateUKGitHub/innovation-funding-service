package org.innovateuk.ifs.form.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.form.domain.FormValidator;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class FormValidatorBuilder extends BaseBuilder<FormValidator, FormValidatorBuilder> {

    private FormValidatorBuilder(List<BiConsumer<Integer, FormValidator>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormValidatorBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormValidator>> actions) {
        return new FormValidatorBuilder(actions);
    }

    @Override
    protected FormValidator createInitial() {
        return new FormValidator();
    }

    public static FormValidatorBuilder newFormValidator() {
        return new FormValidatorBuilder(emptyList());
    }

    public FormValidatorBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public FormValidatorBuilder withTitle(String... value) {
        return withArraySetFieldByReflection("title", value);
    }

    public FormValidatorBuilder withClazzName(String... value) {
        return withArraySetFieldByReflection("clazzName", value);
    }
}
