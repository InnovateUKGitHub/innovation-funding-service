package com.worth.ifs.finance.spendprofile.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * View model backing the internal Finance Team members view of the Spend Profile summary page
 */
public class ProjectSpendProfileViewModel {

    private Long projectId;
    private CompetitionSummaryResource competitionSummary;
    private List<Pair<Long, String>> partnerOrganisationIdsAndNames;

    public ProjectSpendProfileViewModel(Long projectId, CompetitionSummaryResource competitionSummary, List<OrganisationResource> partnerOrganisations) {
        this.projectId = projectId;
        this.competitionSummary = competitionSummary;
        this.partnerOrganisationIdsAndNames = simpleMap(partnerOrganisations, org -> Pair.of(org.getId(), org.getName()));
    }

    public Long getProjectId() {
        return projectId;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public List<Pair<Long, String>> getPartnerOrganisationIdsAndNames() {
        return partnerOrganisationIdsAndNames;
    }
}
