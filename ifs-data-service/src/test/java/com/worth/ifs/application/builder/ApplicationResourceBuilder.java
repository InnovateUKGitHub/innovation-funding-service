package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ApplicationResourceBuilder extends BaseBuilder<ApplicationResource, ApplicationResourceBuilder> {

    private ApplicationResourceBuilder(List<BiConsumer<Integer, ApplicationResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationResourceBuilder newApplicationResource() {
        return new ApplicationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationResource>> actions) {
        return new ApplicationResourceBuilder(actions);
    }

    @Override
    protected ApplicationResource createInitial() {
        return new ApplicationResource();
    }

    public ApplicationResourceBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }

    public ApplicationResourceBuilder withCompetition(Long... competitionIds) {
        return withArray((competition, application) -> setField("competition", competition, application), competitionIds);
    }

    public ApplicationResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatus) {
        return withArray((status, application) -> application.setCompetitionStatus(status), competitionStatus);
    }

    public ApplicationResourceBuilder withApplicationStatus(ApplicationStatusConstants... applicationStatus) {
        return withArray((applicationState, application) -> application.setApplicationStatusConstant(applicationState), applicationStatus);
    }
    public ApplicationResourceBuilder withApplicationStatus(Long... applicationStatus) {
        return withArray((applicationState, application) -> application.setApplicationStatus(applicationState), applicationStatus);
    }

    public ApplicationResourceBuilder withStartDate(LocalDate... dates) {
        return withArray((date, application) -> application.setStartDate(date), dates);
    }

    public ApplicationResourceBuilder withName(String... names) {
        return withArray((name, application) -> setField("name", name, application), names);
    }

    public ApplicationResourceBuilder withSubmittedDate(LocalDateTime... submittedDates) {
        return withArray((submittedDate, application) -> setField("submittedDate", submittedDate, application), submittedDates);
    }

    public ApplicationResourceBuilder withDuration(Long... durations) {
        return withArray((duration, application) -> setField("durationInMonths", duration, application), durations);
    }

    public ApplicationResourceBuilder withCompetitionName(String... competitionNames) {
        return withArray((competitionName, application) -> setField("competitionName", competitionName, application), competitionNames);
    }

    public ApplicationResourceBuilder withAssessorFeedbackFileEntry(Long... assessorFeedbackFileEntryId) {
        return withArray((fileEntryId, application) -> application.setAssessorFeedbackFileEntry(fileEntryId), assessorFeedbackFileEntryId);
    }

    public ApplicationResourceBuilder withCompletion(final BigDecimal... bigDecimals) {
        return withArray((completion, application) -> setField("completion", completion, application), bigDecimals);
    }
}
