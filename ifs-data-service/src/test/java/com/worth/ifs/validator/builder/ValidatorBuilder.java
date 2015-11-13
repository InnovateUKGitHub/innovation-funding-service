package com.worth.ifs.validator.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.validator.domain.Validator;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ValidatorBuilder  extends BaseBuilder<Validator, ValidatorBuilder> {
    private ValidatorBuilder(List<BiConsumer<Integer, Validator>> multiActions) {
        super(multiActions);
    }

    public static ValidatorBuilder newValidator() {
        return new ValidatorBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ValidatorBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Validator>> actions) {
        return new ValidatorBuilder(actions);
    }

    @Override
    protected Validator createInitial() {
        return new Validator();
    }
}
