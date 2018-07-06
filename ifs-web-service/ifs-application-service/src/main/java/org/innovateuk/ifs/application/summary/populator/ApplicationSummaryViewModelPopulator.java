package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.common.populator.SummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;

@Component
public class ApplicationSummaryViewModelPopulator {

    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private SectionService sectionService;
    private UserService userService;
    private SummaryViewModelPopulator summaryViewModelPopulator;
    private ProjectService projectService;

    public ApplicationSummaryViewModelPopulator(ApplicationService applicationService,
                                                CompetitionService competitionService,
                                                SectionService sectionService,
                                                UserService userService,
                                                SummaryViewModelPopulator summaryViewModelPopulator,
                                                ProjectService projectService) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.sectionService = sectionService;
        this.userService = userService;
        this.summaryViewModelPopulator = summaryViewModelPopulator;
        this.projectService = projectService;
    }

    public ApplicationSummaryViewModel populate (long applicationId, UserResource user, ApplicationForm form) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        ProjectResource project = projectService.getByApplicationId(applicationId);
        boolean projectWithdrawn = (project != null && project.isWithdrawn());

        boolean applicationReadyForSubmit = applicationService.isApplicationReadyForSubmit(application.getId());

        SummaryViewModel summaryViewModel = summaryViewModelPopulator.populate(applicationId, user, form);

        //boolean supportUser = user.hasRole(Role.SUPPORT);

        Boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), application);
        //Boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), application) || supportUser;

        return new ApplicationSummaryViewModel(
                application,
                competition,
                applicationReadyForSubmit,
                summaryViewModel,
                userIsLeadApplicant,
                projectWithdrawn);
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
