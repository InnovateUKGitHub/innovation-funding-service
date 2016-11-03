package com.worth.ifs.competitiontemplate.builder;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competitiontemplate.domain.FormInputTemplate;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.domain.FormValidator;

public class FormInputTemplateBuilder extends BaseBuilder<FormInputTemplate, FormInputTemplateBuilder> {

    private FormInputTemplateBuilder(List<BiConsumer<Integer, FormInputTemplate>> newMultiActions) {
        super(newMultiActions);
    }

    public static FormInputTemplateBuilder newFormInputTemplate() {
        return new FormInputTemplateBuilder(emptyList()).with(uniqueIds());
    }

    public FormInputTemplateBuilder withFormInputType(FormInputType formInputType) {
        return with(formInputTemplate -> setField("formInputType", formInputType, formInputTemplate));
    }
    
    public FormInputTemplateBuilder withInputValidators(Set<FormValidator> inputValidators) {
        return with(formInputTemplate -> setField("inputValidators", inputValidators, formInputTemplate));
    }
    
    public FormInputTemplateBuilder withGuidanceQuestion(String guidanceQuestion) {
        return with(formInputTemplate -> setField("guidanceQuestion", guidanceQuestion, formInputTemplate));
    }
    
    public FormInputTemplateBuilder withGuidanceAnswer(String guidanceAnswer) {
        return with(formInputTemplate -> setField("guidanceAnswer", guidanceAnswer, formInputTemplate));
    }
    
    public FormInputTemplateBuilder withDescription(String description) {
        return with(formInputTemplate -> setField("description", description, formInputTemplate));
    }
    
    public FormInputTemplateBuilder withIncludedInApplicationSummary(Boolean includedInApplicationSummary) {
        return with(formInputTemplate -> setField("includedInApplicationSummary", includedInApplicationSummary, formInputTemplate));
    }
    
    @Override
    protected FormInputTemplateBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputTemplate>> actions) {
        return new FormInputTemplateBuilder(actions);
    }

    @Override
    protected FormInputTemplate createInitial() {
        return new FormInputTemplate();
    }

    public FormInputTemplateBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

}
