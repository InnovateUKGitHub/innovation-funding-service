package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.user.domain.ProcessRole;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationStatisticsBuilder extends BaseBuilder<ApplicationStatistics, ApplicationStatisticsBuilder> {

    private ApplicationStatisticsBuilder(List<BiConsumer<Integer,ApplicationStatistics>> multiActions) {
        super(multiActions);
    }

    public static ApplicationStatisticsBuilder newApplicationStatistics() {
        return new ApplicationStatisticsBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Application "));
    }

    @Override
    protected ApplicationStatisticsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationStatistics>> actions) {
        return new ApplicationStatisticsBuilder(actions);
    }

    @Override
    protected ApplicationStatistics createInitial() {
        return new ApplicationStatistics();
    }

    public ApplicationStatisticsBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public ApplicationStatisticsBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public ApplicationStatisticsBuilder withCompetition(Long... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }

    public ApplicationStatisticsBuilder withProcessRoles(List<ProcessRole>... processRoles) {
        return withArraySetFieldByReflection("processRoles", processRoles);
    }

    public ApplicationStatisticsBuilder withAssessments(List<Assessment>... assessments) {
        return withArraySetFieldByReflection("assessments", assessments);
    }
}
