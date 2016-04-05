package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionResourceBuilder extends BaseBuilder<CompetitionResource, CompetitionResourceBuilder> {

    private CompetitionResourceBuilder(List<BiConsumer<Integer, CompetitionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionResourceBuilder newCompetitionResource() {
        return new CompetitionResourceBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionResourceBuilder withSections(List<Long> sections) {
        return with(competition -> competition.setSections(sections));
    }

    public CompetitionResourceBuilder withStartDate(LocalDateTime startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }

    public CompetitionResourceBuilder withEndDate(LocalDateTime endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }

    public CompetitionResourceBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public CompetitionResourceBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public CompetitionResourceBuilder withDescription(String... descriptions) {
        return withArray((description, object) -> setField("description", description, object), descriptions);
    }

    public CompetitionResourceBuilder withAssessmentStartDate(LocalDateTime... assessmentStartDates) {
        return withArray((assessmentStartDate, object) -> setField("assessmentStartDate", assessmentStartDate, object), assessmentStartDates);
    }

    public CompetitionResourceBuilder withAssessmentEndDate(LocalDateTime... assessmentEndDates) {
        return withArray((assessmentEndDate, object) -> setField("assessmentEndDate", assessmentEndDate, object), assessmentEndDates);
    }

    public CompetitionResourceBuilder withMaxResearchRatio(Integer... maxResearchRatios) {
        return withArray((maxResearchRatio, object) -> setField("maxResearchRatio", maxResearchRatio, object), maxResearchRatios);
    }

    public CompetitionResourceBuilder withAcademicGrantClaimPercentage(Integer... grantClaimPercentages) {
        return withArray((grantClaimPercentage, object) -> setField("academicGrantPercentage", grantClaimPercentage, object), grantClaimPercentages);
    }

    @Override
    protected CompetitionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionResource>> actions) {
        return new CompetitionResourceBuilder(actions);
    }

    @Override
    protected CompetitionResource createInitial() {
        return new CompetitionResource();
    }
}
