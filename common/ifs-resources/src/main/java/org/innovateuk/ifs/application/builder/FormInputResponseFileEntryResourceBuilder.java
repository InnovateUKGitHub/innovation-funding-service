package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class FormInputResponseFileEntryResourceBuilder extends BaseBuilder<FormInputResponseFileEntryResource, FormInputResponseFileEntryResourceBuilder> {

    private FormInputResponseFileEntryResourceBuilder(List<BiConsumer<Integer, FormInputResponseFileEntryResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputResponseFileEntryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResponseFileEntryResource>> actions) {
        return new FormInputResponseFileEntryResourceBuilder(actions);
    }

    public static FormInputResponseFileEntryResourceBuilder newFormInputResponseFileEntryResource() {
        return new FormInputResponseFileEntryResourceBuilder(emptyList());
    }

    public FormInputResponseFileEntryResourceBuilder withFileEntryResource(FileEntryResource... fileEntryResources) {
        return withArraySetFieldByReflection("fileEntryResource", fileEntryResources);
    }

    public FormInputResponseFileEntryResourceBuilder withCompoundId(FormInputResponseFileEntryId... compoundIds) {
        return withArraySetFieldByReflection("compoundId", compoundIds);
    }

    @Override
    protected FormInputResponseFileEntryResource createInitial() {
        return new FormInputResponseFileEntryResource();
    }

}
