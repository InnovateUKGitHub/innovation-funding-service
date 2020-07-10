package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedValues;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public FormInputResponseResourceBuilder withQuestion(Long... questions) {
        return withArraySetFieldByReflection("question", questions);
    }

    public FormInputResponseResourceBuilder withFormInputs(Long... owningFormInputs) {
        return withFormInputs(asList(owningFormInputs));
    }

    public FormInputResponseResourceBuilder withFormInputs(List<Long> owningFormInputs) {
        return withList(owningFormInputs, (formInput, formInputResponseResource) -> formInputResponseResource.setFormInput(formInput));
    }

    public FormInputResponseResourceBuilder withApplication(Long applicationId) {
        return with(response -> response.setApplication(applicationId));
    }

    public FormInputResponseResourceBuilder withFormInputMaxWordCount(Integer formInputMaxWordCount) {
        return with(response -> response.setFormInputMaxWordCount(formInputMaxWordCount));
    }

    public FormInputResponseResourceBuilder withUpdateDate(ZonedDateTime dateTime) {
        return with(response -> response.setUpdateDate(dateTime));
    }

    public FormInputResponseResourceBuilder withUpdatedBy(Long processRoleId) {
        return with(response -> response.setUpdatedBy(processRoleId));
    }

    public FormInputResponseResourceBuilder withUpdatedByUser(Long userId) {
        return with(response -> response.setUpdatedByUser(userId));
    }

    public FormInputResponseResourceBuilder withUpdatedByUserName(String userName) {
        return with(response -> response.setUpdatedByUserName(userName));
    }

    public FormInputResponseResourceBuilder withValue(String value) {
        return with(response -> response.setValue(value));
    }
    public FormInputResponseResourceBuilder withFileEntries(List<FileEntryResource> fileEntryResources) {
        return with(response -> response.setFileEntries(fileEntryResources));
    }

    @Override
    protected FormInputResponseResource createInitial() {
        return new FormInputResponseResource();
    }

}
