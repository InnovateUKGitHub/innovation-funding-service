package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overview.viewmodel.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;


/**
 * view model for the application overview page
 */
@Component
public class ApplicationOverviewModelPopulator {

    private CompetitionService competitionService;
    private ProcessRoleService processRoleService;
    private OrganisationService organisationService;
    private SectionService sectionService;
    private ProjectService projectService;
    private CategoryRestService categoryRestService;
    private ApplicationOverviewSectionModelPopulator applicationOverviewSectionModelPopulator;
    private ApplicationOverviewCompletedDetailsModelPopulator applicationOverviewCompletedDetailsModelPopulator;
    private ApplicationOverviewAssignableModelPopulator applicationOverviewAssignableModelPopulator;
    private ApplicationOverviewUserModelPopulator applicationOverviewUserModelPopulator;

    public ApplicationOverviewModelPopulator(CompetitionService competitionService,
                                             ProcessRoleService processRoleService,
                                             OrganisationService organisationService,
                                             SectionService sectionService,
                                             ProjectService projectService,
                                             CategoryRestService categoryRestService,
                                             ApplicationOverviewSectionModelPopulator applicationOverviewSectionModelPopulator,
                                             ApplicationOverviewCompletedDetailsModelPopulator applicationOverviewCompletedDetailsModelPopulator,
                                             ApplicationOverviewAssignableModelPopulator applicationOverviewAssignableModelPopulator,
                                             ApplicationOverviewUserModelPopulator applicationOverviewUserModelPopulator) {
        this.competitionService = competitionService;
        this.processRoleService = processRoleService;
        this.organisationService = organisationService;
        this.sectionService = sectionService;
        this.projectService = projectService;
        this.categoryRestService = categoryRestService;
        this.applicationOverviewSectionModelPopulator = applicationOverviewSectionModelPopulator;
        this.applicationOverviewCompletedDetailsModelPopulator = applicationOverviewCompletedDetailsModelPopulator;
        this.applicationOverviewAssignableModelPopulator = applicationOverviewAssignableModelPopulator;
        this.applicationOverviewUserModelPopulator = applicationOverviewUserModelPopulator;
    }

    public ApplicationOverviewViewModel populateModel(ApplicationResource application, Long userId, ApplicationForm form){
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);
        ProjectResource projectResource = projectService.getByApplicationId(application.getId());
        boolean projectWithdrawn = (projectResource != null && projectResource.isWithdrawn());

        ApplicationOverviewUserViewModel userViewModel = applicationOverviewUserModelPopulator.populate(application, userId);
        ApplicationOverviewAssignableViewModel assignableViewModel = applicationOverviewAssignableModelPopulator.populate(application, userOrganisation, userId);
        ApplicationOverviewCompletedViewModel completedViewModel = applicationOverviewCompletedDetailsModelPopulator.populate(application, userOrganisation);
        ApplicationOverviewSectionViewModel sectionViewModel = applicationOverviewSectionModelPopulator.populate(competition, application, userId);
        Long yourFinancesSectionId = getYourFinancesSectionId(application);

        int completedQuestionsPercentage = application.getCompletion() == null ? 0 : application.getCompletion().intValue();

        List<ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccess();

        return new ApplicationOverviewViewModel(
                application.getId(),
                application.getName(),
                application.getApplicationState(),
                application.isSubmitted(),
                projectWithdrawn,
                competition,
                userOrganisation.orElse(null),
                completedQuestionsPercentage,
                yourFinancesSectionId,
                userViewModel,
                assignableViewModel,
                completedViewModel,
                sectionViewModel,
                researchCategories);
    }

    private Long getYourFinancesSectionId(ApplicationResource application) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .findFirst()
                .map(SectionResource::getId)
                .orElse(null);
    }
}
