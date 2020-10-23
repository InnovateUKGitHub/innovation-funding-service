package org.innovateuk.ifs.management.supporters.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.pagination.PaginationViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class AssignSupportersViewModel {

    private final Long competitionId;
    private final String competitionName;
    private final Long applicationId;
    private final String applicationName;
    private final String innovationArea;
    private final SupportersAvailableForApplicationPageResource supportersAvailableForApplicationPage;
    private final String filter;
    private final List<String> partners;

    public AssignSupportersViewModel(CompetitionResource competition, ApplicationResource application, String filter,
                                    SupportersAvailableForApplicationPageResource supportersAvailableForApplicationPage,
                                    List<OrganisationResource> organisations) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.innovationArea = application.getInnovationArea().getSectorName();
        this.filter = filter;
        this.supportersAvailableForApplicationPage = supportersAvailableForApplicationPage;
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

    public SupportersAvailableForApplicationPageResource getSupportersAvailableForApplicationPage() {
        return supportersAvailableForApplicationPage;
    }

    public PaginationViewModel getPagination() {
        return new PaginationViewModel(supportersAvailableForApplicationPage);
    }

    public List<String> getPartners() {
        return partners;
    }
}
