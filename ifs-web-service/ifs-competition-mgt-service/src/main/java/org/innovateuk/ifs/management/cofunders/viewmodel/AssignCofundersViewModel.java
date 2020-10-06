package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.List;
import java.util.stream.Collectors;

public class AssignCofundersViewModel {

    private final Long competitionId;
    private final String competitionName;
    private final Long applicationId;
    private final String applicationName;
    private final String innovationArea;
    private final CofundersAvailableForApplicationPageResource cofundersAvailableForApplicationPage;
    private final String filter;
    private final List<String> partners;

    public AssignCofundersViewModel(CompetitionResource competition, ApplicationResource application, String filter,
                                    CofundersAvailableForApplicationPageResource cofundersAvailableForApplicationPage,
                                    List<OrganisationResource> organisations) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.innovationArea = application.getInnovationArea().getSectorName();
        this.filter = filter;
        this.cofundersAvailableForApplicationPage = cofundersAvailableForApplicationPage;
        this.partners = organisations.stream().map(org -> org.getName()).collect(Collectors.toList());
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public String getFilter() {
        return filter;
    }

    public CofundersAvailableForApplicationPageResource getCofundersAvailableForApplicationPage() {
        return cofundersAvailableForApplicationPage;
    }

    public Pagination getPagination() {
        return new Pagination(cofundersAvailableForApplicationPage);
    }

    public List<String> getPartners() {
        return partners;
    }
}
