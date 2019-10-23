package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorCompetitionDashboardResourceBuilder extends BaseBuilder<AssessorCompetitionDashboardResource, AssessorCompetitionDashboardResourceBuilder> {

    public static AssessorCompetitionDashboardResourceBuilder newAssessorCompetitionDashboardResource() {
        return new AssessorCompetitionDashboardResourceBuilder(emptyList());
    }

    protected AssessorCompetitionDashboardResourceBuilder(List<BiConsumer<Integer, AssessorCompetitionDashboardResource>> newActions) {
        super(newActions);
    }

    @Override
    protected AssessorCompetitionDashboardResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCompetitionDashboardResource>> actions) {
        return new AssessorCompetitionDashboardResourceBuilder(actions);
    }

    @Override
    protected AssessorCompetitionDashboardResource createInitial() {
        return new AssessorCompetitionDashboardResource();
    }

    public AssessorCompetitionDashboardResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArraySetFieldByReflection("competitionId", competitionIds);
    }

    public AssessorCompetitionDashboardResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public AssessorCompetitionDashboardResourceBuilder withInnovationLead(String... innovationLeads) {
        return withArraySetFieldByReflection("innovationLead", innovationLeads);
    }

    public AssessorCompetitionDashboardResourceBuilder withAssessorAcceptDate(ZonedDateTime... assessorAcceptDate) {
        return withArraySetFieldByReflection("assessorAcceptDate", assessorAcceptDate);
    }

    public AssessorCompetitionDashboardResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDate) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDate);
    }

    public AssessorCompetitionDashboardResourceBuilder withApplicationAssessments(List<ApplicationAssessmentResource>... applicationAssessments) {
        return withArraySetFieldByReflection("applicationAssessments", applicationAssessments);
    }
}
