package com.worth.ifs.bankdetails.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.bankdetails.resource.BankDetailsStatusResource;
import com.worth.ifs.bankdetails.resource.ProjectBankDetailsStatusSummary;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class ProjectBankDetailsStatusSummaryBuilder extends BaseBuilder<ProjectBankDetailsStatusSummary, ProjectBankDetailsStatusSummaryBuilder> {
    public ProjectBankDetailsStatusSummaryBuilder(List<BiConsumer<Integer, ProjectBankDetailsStatusSummary>> newActions) {
        super(newActions);
    }

    @Override
    protected ProjectBankDetailsStatusSummaryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectBankDetailsStatusSummary>> actions) {
        return new ProjectBankDetailsStatusSummaryBuilder(actions);
    }

    @Override
    protected ProjectBankDetailsStatusSummary createInitial() {
        return new ProjectBankDetailsStatusSummary();
    }

    public static ProjectBankDetailsStatusSummaryBuilder newProjectBankDetailsStatusSummary(){
        return new ProjectBankDetailsStatusSummaryBuilder(emptyList());
    }

    public ProjectBankDetailsStatusSummaryBuilder withCompetitionId(Long... competitionIds) {
        return withArray((competitionId, bankDetailsSummary) -> setField("competitionId", competitionId, bankDetailsSummary), competitionIds);
    }

    public ProjectBankDetailsStatusSummaryBuilder withCompetitionName(String... competitionNames) {
        return withArray((competitionName, bankDetailsSummary) -> setField("competitionName", competitionName, bankDetailsSummary), competitionNames);
    }

    public ProjectBankDetailsStatusSummaryBuilder withProjectId(Long... projectIds) {
        return withArray((projectId, bankDetailsSummary) -> setField("projectId", projectId, bankDetailsSummary), projectIds);
    }

    @SafeVarargs
    public final ProjectBankDetailsStatusSummaryBuilder withBankDetailsStatusResources(List<BankDetailsStatusResource>... bankDetailsStatusResourcesList){
        return withArray((bankDetailsStatusResources, bankDetailsSummary) -> setField("bankDetailsStatusResources", bankDetailsStatusResources, bankDetailsSummary), bankDetailsStatusResourcesList);
    }
}
