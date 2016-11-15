package com.worth.ifs.application.builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.FundingDecisionStatus;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.user.domain.ProcessRole;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class ApplicationBuilder extends BaseBuilder<Application, ApplicationBuilder> {

    private ApplicationBuilder(List<BiConsumer<Integer, Application>> multiActions) {
        super(multiActions);
    }

    public static ApplicationBuilder newApplication() {
        return new ApplicationBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("com.worth.ifs.Application "));
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

    public ApplicationBuilder withApplicationStatus(ApplicationStatus... applicationStatus) {
        return withArray((applicationState, application) -> application.setApplicationStatus(applicationState), applicationStatus);
    }

    public ApplicationBuilder withApplicationStatus(ApplicationStatusConstants... applicationStatus) {
        return withArray((applicationState, application) -> {

            ApplicationStatus status = new ApplicationStatus(applicationState.getId(), applicationState.getName());
            application.setApplicationStatus(status);

        }, applicationStatus);
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
}
