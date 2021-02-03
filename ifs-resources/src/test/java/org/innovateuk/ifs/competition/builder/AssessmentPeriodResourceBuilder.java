package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentPeriodResourceBuilder extends BaseBuilder<AssessmentPeriodResource, AssessmentPeriodResourceBuilder> {

    private AssessmentPeriodResourceBuilder (List<BiConsumer<Integer, AssessmentPeriodResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static AssessmentPeriodResourceBuilder newAssessmentPeriodResource() {
        return new AssessmentPeriodResourceBuilder(emptyList()).with(uniqueIds());
    }

    public AssessmentPeriodResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public AssessmentPeriodResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArray((competitionId, object) -> BaseBuilderAmendFunctions.setField("competitionId", competitionId, object), competitionIds);
    }

    public AssessmentPeriodResourceBuilder withIndex(Integer... indexes) {
        return withArray((id, object) -> BaseBuilderAmendFunctions.setField("index", id, object), indexes);
    }

    @Override
    protected AssessmentPeriodResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPeriodResource>> actions) {
        return new AssessmentPeriodResourceBuilder(actions);
    }

    @Override
    protected AssessmentPeriodResource createInitial() {
        return new AssessmentPeriodResource();
    }
}
