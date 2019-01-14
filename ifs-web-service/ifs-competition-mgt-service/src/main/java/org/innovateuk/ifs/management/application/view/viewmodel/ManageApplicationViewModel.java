package org.innovateuk.ifs.management.application.view.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.resource.AppendixResource;
import org.innovateuk.ifs.application.viewmodel.researchCategory.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

public class ManageApplicationViewModel {

    private final SummaryViewModel summaryViewModel;
    private final String backUrl;
    private final String originQuery;
    private final boolean readOnly;
    private final boolean canReinstate;
    private final boolean stakeholder;
    private final ApplicationOverviewIneligibilityViewModel ineligibility;
    private final ResearchCategorySummaryViewModel researchCategorySummaryViewModel;
    private final List<AppendixResource> appendices;
    private final boolean collaborativeProject;
    private final CompetitionResource currentCompetition;

    public ManageApplicationViewModel(SummaryViewModel summaryViewModel,
                                      String backUrl,
                                      String originQuery,
                                      boolean readOnly,
                                      boolean canReinstate,
                                      boolean stakeholder,
                                      ApplicationOverviewIneligibilityViewModel ineligibility,
                                      ResearchCategorySummaryViewModel researchCategorySummaryViewModel,
                                      List<AppendixResource> appendices,
                                      boolean collaborativeProject,
                                      CompetitionResource currentCompetition) {
        this.summaryViewModel = summaryViewModel;
        this.backUrl = backUrl;
        this.originQuery = originQuery;
        this.readOnly = readOnly;
        this.canReinstate = canReinstate;
        this.stakeholder = stakeholder;
        this.ineligibility = ineligibility;
        this.researchCategorySummaryViewModel = researchCategorySummaryViewModel;
        this.appendices = appendices;
        this.collaborativeProject = collaborativeProject;
        this.currentCompetition = currentCompetition;
    }

    public SummaryViewModel getSummaryViewModel() {
        return summaryViewModel;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCanReinstate() {
        return canReinstate;
    }

    public boolean isStakeholder() {
        return stakeholder;
    }

    public ApplicationOverviewIneligibilityViewModel getIneligibility() {
        return ineligibility;
    }

    public ResearchCategorySummaryViewModel getResearchCategorySummaryViewModel() {
        return researchCategorySummaryViewModel;
    }

    public List<AppendixResource> getAppendices() {
        return appendices;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }
}
