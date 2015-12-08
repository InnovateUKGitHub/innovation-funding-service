package com.worth.ifs.file.resource.builders;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.file.resource.FormInputResponseFileResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class FormInputResponseFileResourceBuilder extends BaseBuilder<FormInputResponseFileResource, FormInputResponseFileResourceBuilder> {

    private FormInputResponseFileResourceBuilder(List<BiConsumer<Integer, FormInputResponseFileResource>> multiActions) {
        super(multiActions);
    }

    public static FormInputResponseFileResourceBuilder newFormInputResponseFileResource() {
        return new FormInputResponseFileResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected FormInputResponseFileResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResponseFileResource>> actions) {
        return new FormInputResponseFileResourceBuilder(actions);
    }

    @Override
    protected FormInputResponseFileResource createInitial() {
        return new FormInputResponseFileResource();
    }

    public FormInputResponseFileResourceBuilder withFormInputResponseId(Long... ids) {
        return withArray((id, file) -> setField("formInputResponseId", id, file), ids);
    }
}