package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public ApplicationResourceBuilder withApplicationState(ApplicationState... applicationStates) {
        return withArray((applicationState, application) -> application.setApplicationState(applicationState), applicationStates);
    }

    public ApplicationResourceBuilder withStartDate(LocalDate... dates) {
        return withArray((date, application) -> application.setStartDate(date), dates);
    }

    public ApplicationResourceBuilder withName(String... names) {
        return withArray((name, application) -> setField("name", name, application), names);
    }

    public ApplicationResourceBuilder withSubmittedDate(ZonedDateTime... submittedDates) {
        return withArray((submittedDate, application) -> setField("submittedDate", submittedDate, application), submittedDates);
    }

    public ApplicationResourceBuilder withDurationInMonths(Long... durations) {
        return withArray((duration, application) -> setField("durationInMonths", duration, application), durations);
    }

    public ApplicationResourceBuilder withCompetitionName(String... competitionNames) {
        return withArray((competitionName, application) -> setField("competitionName", competitionName, application), competitionNames);
    }

    public ApplicationResourceBuilder withCompletion(final BigDecimal... bigDecimals) {
        return withArray((completion, application) -> setField("completion", completion, application), bigDecimals);
    }

    public ApplicationResourceBuilder withResearchCategory(ResearchCategoryResource category) {
        return with(applicationResource -> applicationResource.setResearchCategory(category));
    }

    public ApplicationResourceBuilder withInnovationArea(InnovationAreaResource innovationArea) {
        return with(applicationResource -> applicationResource.setInnovationArea(innovationArea));
    }

    public ApplicationResourceBuilder withLeadOrganisationId(Long... leadOrganisationIds) {
        return withArray((leadOrganisationId, application) -> application.setLeadOrganisationId(leadOrganisationId), leadOrganisationIds);
    }

    public ApplicationResourceBuilder withNoInnovationAreaApplicable(Boolean... noInnovationAreaApplicableArray) {
        return withArray((noInnovationAreaApplicable, application) ->
                setField("noInnovationAreaApplicable", noInnovationAreaApplicable, application), noInnovationAreaApplicableArray);
    }

    public ApplicationResourceBuilder withCollaborationLevel(CollaborationLevel... collaborationLevels) {
        return withArray((collaborationLevel, application) -> application.setCollaborationLevel(collaborationLevel),
                collaborationLevels);
    }

    public ApplicationResourceBuilder withCollaborativeProject(Boolean... collaborativeProjectArray) {
        return withArray((collaborativeProject, application) -> application.setCollaborativeProject(collaborativeProject),
                collaborativeProjectArray);
    }

    public ApplicationResourceBuilder withStateAidAgreed(Boolean... stateAidAgreeds) {
        return withArray((stateAidAgreed, application) -> application.setStateAidAgreed(stateAidAgreed), stateAidAgreeds);
    }

    public ApplicationResourceBuilder withResubmission(Boolean... resubmissions) {
        return withArray((resubmission, application) -> setField("resubmission", resubmission, application), resubmissions);
    }

    public ApplicationResourceBuilder withPreviousApplicationNumber(String... numbers) {
        return withArray((number, application) -> setField("previousApplicationNumber", number, application), numbers);
    }

    public ApplicationResourceBuilder withPreviousApplicationTitle(String... titles) {
        return withArray((title, application) -> setField("previousApplicationTitle", title, application), titles);
    }

    public ApplicationResourceBuilder withEvent(String... events) {
        return withArray((event, application) -> application.setEvent(event), events);
    }
    public ApplicationResourceBuilder withLastStateChangeDate(ZonedDateTime... lastStateChangeDates) {
        return withArray((lastStateChangeDate, application) -> application.setLastStateChangeDate(lastStateChangeDate), lastStateChangeDates);
    }

}
