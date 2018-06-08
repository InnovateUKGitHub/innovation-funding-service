package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewCompletedViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;

@Component
public class ApplicationSummaryViewModelPopulator {

    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private SectionService sectionService;
    private SummaryViewModelPopulator summaryViewModelPopulator;

    public ApplicationSummaryViewModelPopulator(ApplicationService applicationService,
                                                CompetitionService competitionService,
                                                SectionService sectionService,
                                                SummaryViewModelPopulator summaryViewModelPopulator) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.sectionService = sectionService;
        this.summaryViewModelPopulator = summaryViewModelPopulator;
    }

    public ApplicationSummaryViewModel populate (long applicationId, UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        boolean applicationReadyForSubmit = applicationService.isApplicationReadyForSubmit(application.getId());

        SummaryViewModel summaryViewModel = summaryViewModelPopulator.populate(applicationId, user);

//        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
//        Set<Long> sectionsMarkedAsComplete = getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);
//
//        boolean allQuestionsCompleted = sectionService.allSectionsMarkedAsComplete(application.getId());
//        boolean userFinanceSectionCompleted = isUserFinanceSectionCompleted(application, userOrganisation.get(), completedSectionsByOrganisation);

        return new ApplicationSummaryViewModel(
                application,
                competition,
                applicationReadyForSubmit,
                summaryViewModel);
    }



    private Set<Long> getCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = new HashSet<>();

        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));

        return combinedMarkedAsComplete;
    }

    private boolean isUserFinanceSectionCompleted(ApplicationResource application, OrganisationResource userOrganisation, Map<Long, Set<Long>> completedSectionsByOrganisation) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .map(SectionResource::getId)
                .anyMatch(id -> completedSectionsByOrganisation.get(userOrganisation.getId()).contains(id));
    }

}
