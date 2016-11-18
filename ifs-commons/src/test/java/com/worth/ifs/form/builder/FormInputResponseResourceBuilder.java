package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.resource.FormInputResponseResource;

import java.time.LocalDateTime;
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

    public FormInputResponseResourceBuilder withFileEntry(FileEntryResource fileEntry) {
        return with(response -> response.setFileEntry(fileEntry.getId()));
    }

    public FormInputResponseResourceBuilder withApplication(Long applicationId) {
        return with(response -> response.setApplication(applicationId));
    }

    public FormInputResponseResourceBuilder withFileName(String fileName) {
        return with(response -> response.setFilename(fileName));
    }

    public FormInputResponseResourceBuilder withFilesizeBytes(Long filesizeBytes) {
        return with(response -> response.setFilesizeBytes(filesizeBytes));
    }

    public FormInputResponseResourceBuilder withFormInputMaxWordCount(Integer formInputMaxWordCount) {
        return with(response -> response.setFormInputMaxWordCount(formInputMaxWordCount));
    }

    public FormInputResponseResourceBuilder withUpdateDate(LocalDateTime dateTime) {
        return with(response -> response.setUpdateDate(dateTime));
    }

    public FormInputResponseResourceBuilder withUpdatedBy(Long processRoleId) {
        return with(response -> response.setUpdatedBy(processRoleId));
    }

    public FormInputResponseResourceBuilder withUpdatedByUserName(String userName) {
        return with(response -> response.setUpdatedByUserName(userName));
    }

    public FormInputResponseResourceBuilder withValue(String value) {
        return with(response -> response.setValue(value));
    }

    @Override
    protected FormInputResponseResource createInitial() {
        return new FormInputResponseResource();
    }
}
