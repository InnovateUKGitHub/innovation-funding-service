package com.worth.ifs.competitiontemplate.builder;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.competitiontemplate.domain.CompetitionTemplate;
import com.worth.ifs.competitiontemplate.domain.QuestionTemplate;
import com.worth.ifs.competitiontemplate.domain.SectionTemplate;

public class SectionTemplateBuilder extends BaseBuilder<SectionTemplate, SectionTemplateBuilder> {

    private SectionTemplateBuilder(List<BiConsumer<Integer, SectionTemplate>> newMultiActions) {
        super(newMultiActions);
    }

    public static SectionTemplateBuilder newSectionTemplate() {
        return new SectionTemplateBuilder(emptyList()).with(uniqueIds());
    }

    public SectionTemplateBuilder withQuestionTemplates(List<QuestionTemplate> questions) {
        return with(competitionTemplate -> competitionTemplate.setQuestionTemplates(questions));
    }

    public SectionTemplateBuilder withSectionType(SectionType sectionType) {
        return with(sectionTemplate -> setField("type", sectionType, sectionTemplate));
    }
    
    public SectionTemplateBuilder withName(String name) {
        return with(sectionTemplate -> setField("name", name, sectionTemplate));
    }
    
    public SectionTemplateBuilder withDescription(String description) {
        return with(sectionTemplate -> setField("description", description, sectionTemplate));
    }
    
    public SectionTemplateBuilder withAssessorGuidanceDescription(String assessorGuidanceDescription) {
        return with(sectionTemplate -> setField("assessorGuidanceDescription", assessorGuidanceDescription, sectionTemplate));
    }
    public SectionTemplateBuilder withParentSectionTemplate(SectionTemplate parentSectionTemplate) {
        return with(sectionTemplate -> setField("parentSectionTemplate", parentSectionTemplate, sectionTemplate));
    }
    
    public SectionTemplateBuilder withChildSectionTemplates(List<SectionTemplate> childSectionTemplates) {
        return with(sectionTemplate -> setField("childSectionTemplates", childSectionTemplates, sectionTemplate));
    }
    
    public SectionTemplateBuilder withCompetitionTemplate(CompetitionTemplate competitionTemplate) {
        return with(sectionTemplate -> setField("competitionTemplate", competitionTemplate, sectionTemplate));
    }
    
    @Override
    protected SectionTemplateBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SectionTemplate>> actions) {
        return new SectionTemplateBuilder(actions);
    }

    @Override
    protected SectionTemplate createInitial() {
        return new SectionTemplate();
    }

    public SectionTemplateBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

}
