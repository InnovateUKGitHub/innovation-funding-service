package org.innovateuk.ifs.application.builder;

import static java.util.Collections.emptyList;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

public class CompetitionSummaryResourceBuilder extends BaseBuilder<CompetitionSummaryResource, CompetitionSummaryResourceBuilder> {

    private CompetitionSummaryResourceBuilder(List<BiConsumer<Integer, CompetitionSummaryResource>> multiActions) {
        super(multiActions);
    }

    public static CompetitionSummaryResourceBuilder newCompetitionSummaryResource() {
        return new CompetitionSummaryResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionSummaryResource>> actions) {
        return new CompetitionSummaryResourceBuilder(actions);
    }

    @Override
    protected CompetitionSummaryResource createInitial() {
        return new CompetitionSummaryResource();
    }

    public CompetitionSummaryResourceBuilder withId(Long... competitionIds) {
        return withArray((competitionId, competition) -> competition.setCompetitionId(competitionId), competitionIds);
    }

    public CompetitionSummaryResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public CompetitionSummaryResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatus) {
        return withArray((competitionState, competition) -> competition.setCompetitionStatus(competitionState), competitionStatus);
    }

    public CompetitionSummaryResourceBuilder withApplicationDeadline(ZonedDateTime... applicationDeadlines) {
        return withArraySetFieldByReflection("applicationDeadline", applicationDeadlines);
    }

    public CompetitionSummaryResourceBuilder withTotalNumberOfApplications(Integer... totalNumberOfApplications) {
        return withArraySetFieldByReflection("totalNumberOfApplications", totalNumberOfApplications);
    }

    public CompetitionSummaryResourceBuilder withApplicationsStarted(Integer... applicationsStarted) {
        return withArraySetFieldByReflection("applicationsStarted", applicationsStarted);
    }

    public CompetitionSummaryResourceBuilder withApplicationsInProgress(Integer... applicationsInProgress) {
        return withArraySetFieldByReflection("applicationsInProgress", applicationsInProgress);
    }

    public CompetitionSummaryResourceBuilder withApplicationsSubmitted(Integer... applicationsSubmitted) {
        return withArraySetFieldByReflection("applicationsSubmitted", applicationsSubmitted);
    }

    public CompetitionSummaryResourceBuilder withApplicationsNotSubmitted(Integer... applicationsNotSubmitted) {
        return withArraySetFieldByReflection("applicationsNotSubmitted", applicationsNotSubmitted);
    }

    public CompetitionSummaryResourceBuilder withApplicationsFunded(Integer... applicationsFunded) {
        return withArraySetFieldByReflection("applicationsFunded", applicationsFunded);
    }

    public CompetitionSummaryResourceBuilder withIneligibleApplications(Integer... ineligibleApplications) {
        return withArraySetFieldByReflection("ineligibleApplications", ineligibleApplications);
    }

    public CompetitionSummaryResourceBuilder withAssesorsInvited(Integer... assessorsInvited) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInvited);
    }
}
