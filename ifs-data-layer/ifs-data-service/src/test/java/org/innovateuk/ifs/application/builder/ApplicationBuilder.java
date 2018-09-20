package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.user.domain.ProcessRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
        return withActivityState(applicationStates);
    }

    public ApplicationBuilder withActivityState(ApplicationState... activityStates) {
        return withArray((activityState, application)
                        -> setField("applicationProcess",
                            new ApplicationProcess(application, null, activityState), application
                ),
                activityStates
        );
    }

    public ApplicationBuilder withStartDate(LocalDate... dates) {
        return withArray((date, application) -> application.setStartDate(date), dates);
    }

    public ApplicationBuilder withProcessRoles(ProcessRole... processRoles) {
        return with(application -> application.setProcessRoles(new ArrayList<>(asList(processRoles))));
    }

    public ApplicationBuilder withName(String... names) {
        return withArray((name, application) -> application.setName(name), names);
    }

    public ApplicationBuilder withApplicationFinancesList(List<ApplicationFinance>... applicationFinancesLists) {
        return withArray((applicationFinancesList, application) -> application.setApplicationFinances(applicationFinancesList), applicationFinancesLists);
    }

    public ApplicationBuilder withFundingDecision(FundingDecisionStatus... fundingDecisionStatus) {
        return withArray((fundingDecision, application) -> application.setFundingDecision(fundingDecision), fundingDecisionStatus);
    }

    public ApplicationBuilder withDurationInMonths(Long... durationInMonths) {
        return withArray((duration, application) -> application.setDurationInMonths(duration), durationInMonths);
    }

    public ApplicationBuilder withManageFundingEmailDate(ZonedDateTime... manageFundingEmailDates) {
        return withArray((fundingEmailDate, application) -> application.setManageFundingEmailDate(fundingEmailDate), manageFundingEmailDates);
    }

    public ApplicationBuilder withAssessmentReviewPanelStatus(Boolean... inAssessmentReviewPanel) {
        return withArray((assessmentPanelStatus, application) -> application.setInAssessmentReviewPanel(assessmentPanelStatus), inAssessmentReviewPanel);
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

    public ApplicationBuilder withInAssessmentReviewPanel(Boolean... inPanels) {
        return withArray((inPanel, application) -> application.setInAssessmentReviewPanel(inPanel), inPanels);
    }
}
