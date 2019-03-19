package org.innovateuk.ifs.form.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class FormInputResourceBuilder extends BaseBuilder<FormInputResource, FormInputResourceBuilder> {

    private FormInputResourceBuilder(List<BiConsumer<Integer, FormInputResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResource>> actions) {
        return new FormInputResourceBuilder(actions);
    }

    @Override
    protected FormInputResource createInitial() {
        return new FormInputResource();
    }

    public static FormInputResourceBuilder newFormInputResource() {
        return new FormInputResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedDescriptions("Description "));
    }

    public FormInputResourceBuilder withId(Long... ids) {
        return withArray((id, formInput) -> formInput.setId(id), ids);
    }

    public FormInputResourceBuilder withWordCount(Integer... wordCounts) {
        return withArray((wordCount, formInput) -> formInput.setWordCount(wordCount), wordCounts);
    }

    public FormInputResourceBuilder withType(FormInputType... value) {
        return withArraySetFieldByReflection("type", value);
    }

    public FormInputResourceBuilder withDescription(String... descriptions) {
        return withArray((description, formInput) -> formInput.setDescription(description), descriptions);
    }

    public FormInputResourceBuilder withQuestion(Long... questions) {
        return withArray((question, formInput) -> formInput.setQuestion(question), questions);
    }

    public FormInputResourceBuilder withCompetition(Long... competitions) {
        return withArray((competition, formInput) -> formInput.setCompetition(competition), competitions);
    }

    public FormInputResourceBuilder withPriority(Integer... priorities) {
        return withArray((priority, formInput) -> formInput.setPriority(priority), priorities);
    }

    public FormInputResourceBuilder withScope(FormInputScope... scopes) {
        return withArray((scope, formInput) -> formInput.setScope(scope), scopes);
    }

    public FormInputResourceBuilder withAllowedFileTypes(Set<FileTypeCategory>... fileTypes) {
        return withArray((types, formInput) -> formInput.setAllowedFileTypes(newLinkedHashSet(types)), fileTypes);
    }

    public FormInputResourceBuilder withInputValidators(Set<Long>... inputValidators) {
        return withArray((inputValidator, formInput) -> formInput.setInputValidators(newLinkedHashSet(inputValidator)), inputValidators);
    }
}