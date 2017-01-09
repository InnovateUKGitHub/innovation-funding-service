package org.innovateuk.ifs.application.builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.user.domain.ProcessRole;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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

    public ApplicationBuilder withAssessment(Assessment... assessments) {
        return with(application -> application.setAssessments(asList(assessments)));
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
