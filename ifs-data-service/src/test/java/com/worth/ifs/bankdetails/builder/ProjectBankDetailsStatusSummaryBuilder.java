package com.worth.ifs.bankdetails.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.bankdetails.resource.ProjectBankDetailsStatusSummary;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
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

    public ProjectBankDetailsStatusSummaryBuilder withProjectIds(Long... projectIds) {
        return withArray((projectId, bankDetailsSummary) -> setField("projectId", projectId, bankDetailsSummary), projectIds);
    }
}
