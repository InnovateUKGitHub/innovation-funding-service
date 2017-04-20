package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

public class ApplicationBuilder extends BaseBuilder<Application, ApplicationBuilder> {

    private ApplicationBuilder(List<BiConsumer<Integer, Application>> multiActions) {
        super(multiActions);
    }

    public static ApplicationBuilder newApplication() {
        return new ApplicationBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Application "));
    }

    @Override
    protected ApplicationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Application>> actions) {
        return new ApplicationBuilder(actions);
    }

    @Override
    protected Application createInitial() {
        return new Application();
    }

    public ApplicationBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }

    public ApplicationBuilder withCompetition(Competition... competitions) {
        return withArray((competition, application) -> application.setCompetition(competition), competitions);
    }

    public ApplicationBuilder withApplicationState(ApplicationState... applicationStates) {
        return withArray((applicationState, application)
                        -> setField("applicationProcess",
                                new ApplicationProcess(application, null, new ActivityState(ActivityType.APPLICATION, applicationState.getBackingState())), application
                ),
                applicationStates
        );
    }

    public ApplicationBuilder withStartDate(LocalDate... dates) {
        return withArray((date, application) -> application.setStartDate(date), dates);
    }

    public ApplicationBuilder withProcessRoles(ProcessRole... processRoles) {
        return with(application -> application.setProcessRoles(asList(processRoles)));
    }

    public ApplicationBuilder withName(String... names) {
        return withArray((name, application) -> application.setName(name), names);
    }

    public ApplicationBuilder withFundingDecision(FundingDecisionStatus... fundingDecisionStatus) {
        return withArray((fundingDecision, application) -> application.setFundingDecision(fundingDecision), fundingDecisionStatus);
    }

    public ApplicationBuilder withAssessorFeedbackFileEntry(FileEntry... fileEntry) {
        return withArray((file, application) -> application.setAssessorFeedbackFileEntry(file), fileEntry);
    }

    public ApplicationBuilder withDurationInMonths(Long... durationInMonths) {
        return withArray((duration, application) -> application.setDurationInMonths(duration), durationInMonths);
    }

    public ApplicationBuilder withManageFundingEmailDate(ZonedDateTime... manageFundingEmailDates) {
        return withArray((fundingEmailDate, application) -> application.setManageFundingEmailDate(fundingEmailDate), manageFundingEmailDates);
    }

    @Override
    public void postProcess(int index, Application built) {

        // add hibernate-style back refs
        if (built.getCompetition() != null) {
            built.getCompetition().getApplications().add(built);
        }
    }

    public ApplicationBuilder withCompletion(BigDecimal... bigDecimals) {
        return withArray((completion, application) -> application.setCompletion(completion), bigDecimals);
    }

    public ApplicationBuilder withInnovationArea(InnovationArea... innovationAreas) {
        return withArray((innovationArea, application) -> application.setInnovationArea(innovationArea), innovationAreas);
    }

    public ApplicationBuilder withNoInnovationAreaApplicable(Boolean... noInnovationAreaApplicable) {
        return withArray((noApplicable, application) -> application.setNoInnovationAreaApplicable(noApplicable), noInnovationAreaApplicable);
    }

    public ApplicationBuilder withResearchCategory(ResearchCategory... researchCategories) {
        return withArray((researchCategory, application) -> application.setResearchCategory(researchCategory), researchCategories);
    }
}
