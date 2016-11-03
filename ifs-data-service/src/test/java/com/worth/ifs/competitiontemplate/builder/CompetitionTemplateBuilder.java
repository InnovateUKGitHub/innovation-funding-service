package com.worth.ifs.competitiontemplate.builder;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.domain.CompetitionType;
import com.worth.ifs.competitiontemplate.domain.CompetitionTemplate;
import com.worth.ifs.competitiontemplate.domain.SectionTemplate;

public class CompetitionTemplateBuilder extends BaseBuilder<CompetitionTemplate, CompetitionTemplateBuilder> {

    private CompetitionTemplateBuilder(List<BiConsumer<Integer, CompetitionTemplate>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionTemplateBuilder newCompetitionTemplate() {
        return new CompetitionTemplateBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionTemplateBuilder withSectionTemplates(List<SectionTemplate> sections) {
        return with(competitionTemplate -> competitionTemplate.setSectionTemplates(sections));
    }

    public CompetitionTemplateBuilder withCompetitionType(CompetitionType competitionType) {
        return with(competitionTemplate -> setField("competitionType", competitionType, competitionTemplate));
    }
    
    @Override
    protected CompetitionTemplateBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionTemplate>> actions) {
        return new CompetitionTemplateBuilder(actions);
    }

    @Override
    protected CompetitionTemplate createInitial() {
        return new CompetitionTemplate();
    }

    public CompetitionTemplateBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

}
