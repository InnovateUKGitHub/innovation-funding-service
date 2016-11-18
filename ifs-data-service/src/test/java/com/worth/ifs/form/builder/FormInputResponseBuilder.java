package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.idBasedValues;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class FormInputResponseBuilder extends BaseBuilder<FormInputResponse, FormInputResponseBuilder> {

    private FormInputResponseBuilder(List<BiConsumer<Integer, FormInputResponse>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputResponseBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResponse>> actions) {
        return new FormInputResponseBuilder(actions);
    }

    public static FormInputResponseBuilder newFormInputResponse() {
        return new FormInputResponseBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedValues("Value "));
    }

    public FormInputResponseBuilder withFormInputs(FormInput... owningFormInputs) {
        return withFormInputs(asList(owningFormInputs));
    }

    public FormInputResponseBuilder withFormInputs(List<FormInput> owningFormInputs) {
        return withList(owningFormInputs, (formInput, formInputResponse) -> formInputResponse.setFormInput(formInput));
    }

    public FormInputResponseBuilder withFileEntry(FileEntry fileEntry) {
        return with(response -> response.setFileEntry(fileEntry));
    }
    
    public FormInputResponseBuilder withValue(String value) {
        return with(response -> response.setValue(value));
    }

    @Override
    protected FormInputResponse createInitial() {
        return new FormInputResponse();
    }
}
