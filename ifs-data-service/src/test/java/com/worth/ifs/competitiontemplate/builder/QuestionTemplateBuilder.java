package com.worth.ifs.competitiontemplate.builder;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competitiontemplate.domain.FormInputTemplate;
import com.worth.ifs.competitiontemplate.domain.QuestionTemplate;

public class QuestionTemplateBuilder extends BaseBuilder<QuestionTemplate, QuestionTemplateBuilder> {

    private QuestionTemplateBuilder(List<BiConsumer<Integer, QuestionTemplate>> newMultiActions) {
        super(newMultiActions);
    }

    public static QuestionTemplateBuilder newQuestionTemplate() {
        return new QuestionTemplateBuilder(emptyList()).with(uniqueIds());
    }

    public QuestionTemplateBuilder withName(String name) {
        return with(questionTemplate -> setField("name", name, questionTemplate));
    }
    
    public QuestionTemplateBuilder withShortName(String shortName) {
        return with(questionTemplate -> setField("shortName", shortName, questionTemplate));
    }
    
    public QuestionTemplateBuilder withDescription(String description) {
        return with(questionTemplate -> setField("description", description, questionTemplate));
    }
    
    public QuestionTemplateBuilder withAssessorGuidanceQuestion(String assessorGuidanceQuestion) {
        return with(questionTemplate -> setField("assessorGuidanceQuestion", assessorGuidanceQuestion, questionTemplate));
    }
    
    public QuestionTemplateBuilder withAssessorGuidanceAnswer(String assessorGuidanceAnswer) {
        return with(questionTemplate -> setField("assessorGuidanceAnswer", assessorGuidanceAnswer, questionTemplate));
    }
    
    public QuestionTemplateBuilder withFormInputTemplates(List<FormInputTemplate> formInputTemplates) {
        return with(questionTemplate -> setField("formInputTemplates", formInputTemplates, questionTemplate));
    }
    
    
    @Override
    protected QuestionTemplateBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionTemplate>> actions) {
        return new QuestionTemplateBuilder(actions);
    }

    @Override
    protected QuestionTemplate createInitial() {
        return new QuestionTemplate();
    }

    public QuestionTemplateBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

}
