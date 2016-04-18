package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.idBasedValues;
import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class FormInputResponseResourceBuilder extends BaseBuilder<FormInputResponseResource, FormInputResponseResourceBuilder> {

    private FormInputResponseResourceBuilder(List<BiConsumer<Integer, FormInputResponseResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputResponseResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResponseResource>> actions) {
        return new FormInputResponseResourceBuilder(actions);
    }

    public static FormInputResponseResourceBuilder newFormInputResponseResource() {
        return new FormInputResponseResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedValues("Value "));
    }

    public FormInputResponseResourceBuilder withFormInputs(Long... owningFormInputs) {
        return withFormInputs(asList(owningFormInputs));
    }

    public FormInputResponseResourceBuilder withFormInputs(List<Long> owningFormInputs) {
        return withList(owningFormInputs, (formInput, formInputResponseResource) -> formInputResponseResource.setFormInput(formInput));
    }

    public FormInputResponseResourceBuilder withFileEntry(FileEntry fileEntry) {
        return with(response -> response.setFileEntry(fileEntry.getId()));
    }

    @Override
    protected FormInputResponseResource createInitial() {
        return new FormInputResponseResource();
    }
}
